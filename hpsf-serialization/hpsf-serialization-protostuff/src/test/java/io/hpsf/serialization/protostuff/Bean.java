package io.hpsf.serialization.protostuff;

import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * 
 * @author winflex
 */
@Data
public class Bean {
	
	private String field1;
	
	private Map<String, String> field2;
	
	private List<String> field3;
	
}
