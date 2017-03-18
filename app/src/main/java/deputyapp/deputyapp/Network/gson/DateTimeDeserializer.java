package deputyapp.deputyapp.Network.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeDeserializer implements JsonSerializer<Date>, JsonDeserializer<Date>
{
    private static final SimpleDateFormat JSON_DATE_TIME = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZ");

    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        try
        {
            return JSON_DATE_TIME.parse(json.getAsJsonPrimitive().getAsString());
        }
        catch (ParseException pe)
        {
            throw new JsonParseException(pe);
        }
    }

    @Override
    public JsonElement serialize(Date date, Type type, JsonSerializationContext jsonSerializationContext)
    {
        return new JsonPrimitive(JSON_DATE_TIME.format(date));
    }
}
