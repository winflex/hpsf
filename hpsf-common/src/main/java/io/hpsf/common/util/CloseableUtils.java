package io.hpsf.common.util;

import java.io.Closeable;
import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author winflex
 */
@Slf4j
public class CloseableUtils {
	
	
	public static final void closeQuietly(Closeable c) {
		try {
			c.close();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}
	
}
