package io.hpsf.registry.zookeeper;

import static io.hpsf.common.util.CloseableUtils.closeQuietly;
import static org.apache.zookeeper.CreateMode.EPHEMERAL;
import static org.apache.zookeeper.CreateMode.PERSISTENT;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.hpsf.registry.api.AbstractRegsitry;
import org.hpsf.registry.api.INotifyListener.NotifyType;
import org.hpsf.registry.api.IRegistry;
import org.hpsf.registry.api.Registration;
import org.hpsf.registry.api.RegistryConfig;
import org.hpsf.registry.api.RegistryException;
import org.hpsf.registry.api.ServiceMeta;

import io.hpsf.common.Endpoint;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * hpsf
 * |- registry
 *    |- <serviceName>-<serviceVersion>
 *       |- <ip>:<port>
 *       |- <ip>:<port>
 *       |- <ip>:<port>
 *    |- <serviceName>-<serviceVersion>
 *       |- <ip>:<port>
 *       |- <ip>:<port>
 *       |- <ip>:<port>
 * </pre>
 * 
 * @author winflex
 */
@Slf4j
public class ZookeeperRegistry extends AbstractRegsitry implements IRegistry {

	public static final String ROOT = "/hpsf/registry";

	private CuratorFramework curator;

	private final ConcurrentMap<ServiceMeta, PathChildrenCache> pathChildrenCaches = new ConcurrentHashMap<>();

	@Override
	public void init(RegistryConfig config) throws RegistryException {
		curator = CuratorFrameworkFactory.newClient(config.getString("server"),
				new ExponentialBackoffRetry(100, 10, 30000));
		curator.getConnectionStateListenable().addListener(((client, state) -> {
			log.info("zookeeper connection event {}", state);
			if (state == ConnectionState.RECONNECTED) {
				// 重新订阅
				getSubscribers().keySet().forEach(serviceMeta -> {
					try {
						doSubscribe(serviceMeta);
					} catch (RegistryException e) {
						log.error("failed to re-subscribe {}", serviceMeta, e);
					}
				});
				// 重新注册提供者
				getRegistrations().forEach((r -> {
					try {
						doRegister(r);
					} catch (RegistryException e) {
						log.error("failed to re-register {}", r.getServiceMeta(), e);
					}
				}));
			}
		}));
		curator.start();
	}

	@Override
	public void doRegister(Registration registration) throws RegistryException {
		final String directory = path4Service(registration.getServiceMeta());
		// 创建服务目录
		try {
			if (curator.checkExists().forPath(directory) == null) {
				curator.create().creatingParentsIfNeeded().withMode(PERSISTENT).forPath(directory);
			}
		} catch (Exception e) {
			boolean exist = false;
			try {
				exist = curator.checkExists().forPath(directory) != null;
			} catch (Exception t) {

				throw new RegistryException("create service path {} failed", e);
			}
			if (!exist) {
				throw new RegistryException("create service path {} failed", e);
			}
		}

		// 创建提供者节点
		try {
			curator.create().withMode(EPHEMERAL).forPath(path4Endpoint(directory, registration.getEndpoint()));
		} catch (Exception e) {
			throw new RegistryException(e);
		}
	}

	@Override
	public void doUnregister(Registration registration) throws RegistryException {
		final String directory = path4Service(registration.getServiceMeta());
		try {
			if (curator.checkExists().forPath(directory) == null) {
				log.warn("{} don't exists", directory);
				// 路径不存在就不管了
				return;
			}
		} catch (Exception e) {
			throw new RegistryException(e);
		}

		try {
			curator.delete().forPath(path4Endpoint(directory, registration.getEndpoint()));
		} catch (Exception e) {
			throw new RegistryException(e);
		}
	}

	@Override
	protected void doSubscribe(ServiceMeta serviceMeta) throws RegistryException {
		PathChildrenCache pathChildrenCache = pathChildrenCaches.get(serviceMeta);
		if (pathChildrenCache == null) {
			pathChildrenCache = new PathChildrenCache(curator, path4Service(serviceMeta), false);
			PathChildrenCache oldPathChildrenCache = pathChildrenCaches.putIfAbsent(serviceMeta, pathChildrenCache);
			if (oldPathChildrenCache == null) {
				pathChildrenCache.getListenable().addListener((client, event) -> {
					log.info("zookeeper connection event {}", event);
					switch (event.getType()) {
					case CHILD_ADDED: {
						final String fullPath = event.getData().getPath();
						final String path = fullPath.substring(fullPath.lastIndexOf("/") + 1);
						Registration r = new Registration(parseEndpoint(path), serviceMeta);
						notify(r, NotifyType.ONLINE);
						break;
					}
					case CHILD_REMOVED: {
						final String fullPath = event.getData().getPath();
						final String path = fullPath.substring(fullPath.lastIndexOf("/") + 1);
						Registration r = new Registration(parseEndpoint(path), serviceMeta);
						notify(r, NotifyType.OFFLINE);
						break;
					}
					default:
						break;
					}
				});

				try {
					pathChildrenCache.start();
				} catch (Exception e) {
					throw new RegistryException(e);
				}
			} else {
				closeQuietly(pathChildrenCache);
			}
		}
	}

	@Override
	protected void doUnsubscribe(ServiceMeta serviceMeta) throws RegistryException {

	}

	@Override
	public List<Registration> lookup(ServiceMeta serviceMeta) throws RegistryException {
		try {
			List<Registration> registrations = new ArrayList<>();
			curator.getChildren().forPath(path4Service(serviceMeta)).forEach((childPath) -> {
				registrations.add(new Registration(parseEndpoint(childPath), serviceMeta));
			});
			return registrations;
		} catch (Exception e) {
			throw new RegistryException(e);
		}
	}

	private String path4Service(ServiceMeta meta) {
		return ROOT + "/" + meta.directoryString();
	}

	private String path4Endpoint(String directory, Endpoint endpoint) {
		return directory + "/" + endpoint.getIp() + ":" + endpoint.getPort();
	}

	private Endpoint parseEndpoint(String path) {
		String[] slices = path.split(":");
		return new Endpoint(slices[0], Integer.parseInt(slices[1]));
	}

	@Override
	public void close() throws IOException {
		pathChildrenCaches.values().forEach(c -> closeQuietly(c));
		if (curator != null) {
			curator.close();
		}
	}
}
