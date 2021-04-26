import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;

//Класс для разборки сайта vesti.ru на новости
public class Vesti {

    static ArrayList<String> sites = new ArrayList<String>();
    static ArrayList<String> headings = new ArrayList<String>();
    static ArrayList<String> texts = new ArrayList<String>();

    /**
     * Метод, возвращающий содержимое веб-запроса (например, HTM, HTML, ASP, PHP страниц).
     * Если веб-запросы не должны проходить через локальный адрес прокси, тогда прокси не должен передаваться, как параметтр.
     * @param addressWeb   - веб-адрес;
     * @param addressProxy - адрес прокси;
     * @param portalProxy  - портал прокси;
     * @return Каждый возвращенный элемент списка соответствует одной строки запрощенного файла.
     */
    public static ArrayList takeContentFromWEB(String addressWeb, String addressProxy, int portalProxy) {

        ArrayList ans = new ArrayList();

        if (addressProxy != null && addressProxy != "") {
            System.setProperty("http.proxyHost", addressProxy);
            System.setProperty("http.proxyPort", portalProxy + "");
        }
        try {
            URL address = new URL(addressWeb);

            //Создаем соединение с сайтом
            HttpURLConnection connection = (HttpURLConnection) address.openConnection();

            //Подключаемся к сайту
            connection.connect();

            //Получаем контент и кладем в один BufferedReader
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "windows-1251"));

            String s = "";
            while (null != ((s = br.readLine()))) {
                ans.add(s);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ans;
    }

    /**
     * Метод, получающий ссылки на новости.
     * @param site - сайт, с которого необходимо получить ссылки.
     */
    public static void takeNewsAddress(String site) throws Exception {
        //Адрес прокси (если есть)
        String addressProxy = null;
        int portaProxy = 0;

        //Запроса
        List<String> page = takeContentFromWEB(site, addressProxy, portaProxy);

        //Удаляем теги из фраз
        String str = "";
        for (int i = 0; i < page.size(); i++) {
            String s = page.get(i);
            if (s.contains("<h3") && s.contains("b-item__title")) {
                String link = s.split("href=\"")[1].split("\">")[0];
                takeNewsText(link);
            }
        }
    }

    /**
     * Метод, получающий заголовки и тексты новостей.
     * @param site - сайт, с которого надо брать новость.
     */
    public static void takeNewsText(String site) throws Exception {

        if(!site.contains("http"))
            site = "https://www.vesti.ru" + site;

        //Адрес прокси (если есть)
        String addressProxy = null;
        int portaProxy = 0;

        //Запроса
        List<String> page = takeContentFromWEB(site, addressProxy, portaProxy);

        String heading = "";
        String allText = "";

        //Удаляем теги из фраз
        for (int i = 0; i < page.size(); i++) {
            String s = page.get(i);

            //Выбор заголовка новости
            if (s.contains("<h1")) {
                try {
                    heading = s.split(">")[1].split("<")[0];
                } catch (Exception ignored) {
                }
            }

            //Выбор текста новости
            if(s.contains("<p")){
                try {
                    String text = s.split("<p>")[1].split("</p>")[0] + " ";
                    allText += text;
                } catch (Exception ignored) {
                }
            }
        }

        if(heading != "") {
            sites.add(site);
            headings.add(heading);
            allText = allText.replaceAll(";", ".");
            texts.add(allText);

            System.out.println(heading);

            //TODO: удалить из текста всякую гадость
            System.out.println(allText);
        }
    }

    /**
     * Метод, собирающий полученные новости в csv файл.
     * @param separator - символ разделения колонок.
     * @param fileName - имя файла.
     */
    public static void writeToCsvFile(String separator, String fileName){
        try (OutputStreamWriter writer =
                     new OutputStreamWriter(new FileOutputStream(fileName), Charset.forName("cp1251"))) {
            StringBuilder sb = new StringBuilder();
            sb.append("Site");
            sb.append(separator);
            sb.append("Heading");
            sb.append(separator);
            sb.append("Text");
            sb.append(separator);
            sb.append("Value");
            sb.append('\n');

            for(int i = 0; i < sites.size(); ++i) {
                sb.append(sites.get(i));
                sb.append(separator);
                sb.append(headings.get(i));
                sb.append(separator);
                sb.append(texts.get(i));
                sb.append(separator);
                sb.append("True");
                sb.append('\n');
            }

            writer.write(sb.toString());
            System.out.println("DONE!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            takeNewsAddress("https://www.vesti.ru/news/");
            writeToCsvFile(";", "test");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
