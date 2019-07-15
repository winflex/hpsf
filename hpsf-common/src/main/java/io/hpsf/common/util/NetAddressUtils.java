package io.hpsf.common.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * 
 * 
 * @author winflex
 */
public class NetAddressUtils {

	private static final InetAddress LOCAL_ADDRESS;
	static {
		LOCAL_ADDRESS = findLoalAddress();
	}

	private static InetAddress findLoalAddress() {
		InetAddress selectedAddress = null;
		try {
			ArrayList<InetAddress> localAddresses = new ArrayList<>();
			Enumeration<?> enumeration = NetworkInterface.getNetworkInterfaces();
			while (enumeration.hasMoreElements()) {
				NetworkInterface netInterface = (NetworkInterface) enumeration.nextElement();
				Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
				while (addresses.hasMoreElements()) {
					InetAddress address = addresses.nextElement();
					if (!address.isLoopbackAddress() && !address.isLinkLocalAddress()
							&& address instanceof Inet4Address) {
						localAddresses.add(address);
					}
				}
			}

			if (!localAddresses.isEmpty()) {
				for (InetAddress addr : localAddresses) {
					if (!addr.getHostAddress().startsWith("127.0") && !addr.getHostAddress().startsWith("192.168")) {
						selectedAddress = addr;
						break;
					}
				}

				if (selectedAddress == null) {
					selectedAddress = localAddresses.get(0);
				}
			} else {
				selectedAddress = InetAddress.getLocalHost();
			}

		} catch (Exception e) {
		}
		return selectedAddress;
	}

	/**
	 * 遍历网卡，查找一个非回路ip地址并返回
	 */
	public static InetAddress getLocalAddress() {
		return LOCAL_ADDRESS;
	}
}