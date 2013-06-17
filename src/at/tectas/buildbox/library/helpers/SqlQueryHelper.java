package at.tectas.buildbox.library.helpers;

import java.util.HashMap;
import java.util.Map.Entry;

public class SqlQueryHelper {

	public static final String SQL_CREATE_TABLE_KEYWORD = "CREATE TABLE IF NOT EXISTS";
	public static final String SQL_CREATE_INDEX_KEYWORD = "CREATE INDEX IF NOT EXISTS '%s' ON '%s' ('%s' ASC)";
	
	public static String getCreateTableQuery(String tableName, HashMap<String, String> fields) {
		StringBuilder builder = new StringBuilder();
		
		builder.append(SQL_CREATE_TABLE_KEYWORD);
		builder.append(" ");
		builder.append(tableName);
		builder.append("(");
		
		int i = 0;
		
		for (Entry<String, String> entry: fields.entrySet()) {
			builder.append(entry.getKey());
			builder.append(" ");
			builder.append(entry.getValue());
			builder.append(" ");
			
			if (i < (fields.size() -1)) {
				builder.append(", ");
			}
			else {
				builder.append(")");
			}
			
			i++;
		}
		
		return builder.toString();
	}
	
	public static String getCreateIndexQuery(String table, String column) {
		String format = SqlQueryHelper.SQL_CREATE_INDEX_KEYWORD;
		return String.format(format, table + "_" + column, table, column);
	}
}
