/**
 *
 *  @author Zbanyszek Wojciech S16600
 *
 */

package zad1;

import java.io.IOException;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import net.aksingh.owmjapis.CurrentWeather;
import net.aksingh.owmjapis.OpenWeatherMap;

public class Service {
	
	String country;
	String city;
	Map<String, String> waluty = getCurrencies();
	private String[] nbpSite = new String[] { "http://www.nbp.pl/kursy/kursya.html",
	"http://www.nbp.pl/kursy/kursyb.html" };

	public Service(String string) {
		this.country = string;
		this.city = "Warsaw";
	}

	
    public Double getRateFor(String kod_waluty) throws IOException, JSONException {
        String krajRate = waluty.get(country);
        CloseableHttpClient httpMyClient = HttpClients.createDefault();
        HttpGet data = new HttpGet("https://api.exchangeratesapi.io/latest?base=" + kod_waluty + "&symbols=" + krajRate);

        CloseableHttpResponse response = httpMyClient.execute(data);

        HttpEntity entity = response.getEntity();

        if (entity != null){
            String result = EntityUtils.toString(entity);

            JSONObject jsonObj = new JSONObject(result);
            return (double)jsonObj.getJSONObject("rates").get(krajRate);

        }else{
            return null;
        }
    }

	public Double getNBPRate() throws ClientProtocolException, IOException {
		String temp = "";
		CloseableHttpClient client = HttpClients.createDefault();
		for (String tempSite : nbpSite) {
			HttpGet get = new HttpGet(tempSite);
			CloseableHttpResponse respons = client.execute(get);
			HttpEntity entity = respons.getEntity();
			temp += EntityUtils.toString(entity);
			//int indexCurrency = tempData.indexOf(countries.get(country));
			int indexCurrency = temp.indexOf(waluty.get(country));
			if (indexCurrency != -1) {
				int tempidx = temp.indexOf(">", indexCurrency + 10) + 1;
				return Double.valueOf(temp.substring(tempidx, tempidx + 6).replace(',', '.'));
			}
		}
		return 1.0;
	}

	public String getWeather(String city) {
		OpenWeatherMap myOwm = new OpenWeatherMap(OpenWeatherMap.Units.METRIC, "e1062b0ac1617d837933b2d9d1801f80");
		
		try {
			CurrentWeather aktualnaPogoda = myOwm.currentWeatherByCityName(city);         		
			if (aktualnaPogoda.hasBaseStation()) return aktualnaPogoda.getRawResponse();
		} catch (IOException | JSONException e) {
			
		    e.printStackTrace();
		}
		
		return null;
	}
	
	public String getWeatherText(String city) {
		OpenWeatherMap myOwm = new OpenWeatherMap(OpenWeatherMap.Units.METRIC, "e1062b0ac1617d837933b2d9d1801f80");
		String weather = "";
		
		try {
			CurrentWeather aktualnaPogoda = myOwm.currentWeatherByCityName(city);         
			
			if (aktualnaPogoda.hasBaseStation()) {
				JSONObject jsonObj = new JSONObject(aktualnaPogoda.getRawResponse());
				weather += "Temperatura: " + jsonObj.getJSONObject("main").get("temp") + " ";
				weather += "(od " + jsonObj.getJSONObject("main").get("temp_min") + "";
				weather += " do " + jsonObj.getJSONObject("main").get("temp_max") + ") st. C\n";
				weather += "Wilgotność: " + jsonObj.getJSONObject("main").get("humidity") + "%\n";
				weather += "Ciśnienie: " + jsonObj.getJSONObject("main").get("pressure") + " hPa";
			}
		} catch (IOException | JSONException e) {
			
		    e.printStackTrace();
		}
		
		return weather;
	}
	
    private Map<String, String> getCurrencies() {
        Locale[] myLocales = Locale.getAvailableLocales();
        Map<String, String> myCurr = new TreeMap<>();
        for (Locale locale : myLocales) {
            try {
            	myCurr.put(locale.getDisplayCountry(Locale.ENGLISH),
                        Currency.getInstance(locale).getCurrencyCode());
            } catch (Exception e) {
            	//e.printStackTrace();
            }
        }
        return myCurr;
    }
    
	
}
  
