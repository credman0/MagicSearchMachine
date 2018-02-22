package querys;
import org.json.JSONObject;

public abstract class SearchQuery {
	
	
	public abstract boolean matchesQuery(JSONObject card);
}
