package tech.bbwang.www.sqlite;

import java.util.List;
import java.util.Map;

/**
 * 数据表操作类接口
 * 
 * @author bbwang8088@126.com
 * 
 */
public interface IDAO {

	public boolean create();

	public List<Object> getAll();

	public List<Object> getAll(Map<String, String> params);

	public boolean insert(Object data);

	public boolean insert(List<Object> data);

	public Object get(Map<String, String> params);

	public int delete(Map<String, String> params);

	public int update(Map<String, String> setParams, Map<String, String> whereParams);
}
