import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;

//Класс для разборки сайта interfax.ru на новости
public class Interfax {

    private static ArrayList<String> sites = new ArrayList<String>();
    private static ArrayList<String> headings = new ArrayList<String>();
    private static ArrayList<String> texts = new ArrayList<String>();

    /**
     * Метод, возвращающий содержимое веб-запроса (например, HTM, HTML, ASP, PHP страниц).
     * Если веб-запросы не должны проходить через локальный адрес прокси, тогда прокси не должен передаваться, как параметтр.
     * @param addressWeb   - веб-адрес;
     * @param addressProxy - адрес прокси;
     * @param portalProxy  - портал прокси;
     * @return Каждый возвращенный элемент списка соответствует одной строки запрощенного файла.
     */
    private static ArrayList takeContentFromWEB(String addressWeb, String addressProxy, int portalProxy) {

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
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("cp1251")));

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
     * Метод, получающий ссылки на новости и вызывающий для каждой полученной ссылки метод получения заголовков и текстов новостей.
     * @param site - сайт, с которого необходимо получить ссылки.
     */
    private static void takeNewsAddress(String site) throws Exception {
        //Адрес прокси (если есть)
        String addressProxy = null;
        int portaProxy = 0;

        //Запроса
        List<String> page = takeContentFromWEB(site, addressProxy, portaProxy);

        //Удаляем теги из фраз
        String str = "";
        for (int i = 0; i < page.size(); i++) {
            String s = page.get(i);
            if (s.contains("<a") && s.contains("href=")) {
                String link = s.split("href=\"")[1].split("\">")[0];
                if(link.contains("\"")){
                    link = link.split("\"")[0];
                }
                takeNewsText(link);
            }
        }
    }

    /**
     * Метод, получающий заголовки и тексты новостей.
     * @param site - сайт, с которого надо брать новость.
     */
    private static void takeNewsText(String site) throws Exception {

        if(!site.contains("http"))
            site = "https://www.interfax.ru" + site;

        //Адрес прокси (если есть)
        String addressProxy = null;
        int portaProxy = 0;

        //Запроса
        List<String> page = takeContentFromWEB(site, addressProxy, portaProxy);

        String heading = "";
        String allText = "";

        for (int i = 0; i < page.size(); i++) {
            String s = page.get(i);

            //Выбор заголовка новости
            if (s.contains("headline")) {
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

            System.out.println(heading);

            //удаляем из текста всякую гадость
            if(allText.contains("href")){
                String buf = "";
                int i = 0;
                while (i < allText.length()){
                    if(allText.charAt(i) == '<'){
                        while(allText.charAt(i) != '>')
                            ++i;
                    }else{
                        buf += allText.charAt(i);
                    }
                    ++i;
                }
                allText = buf;
            }
            System.out.println(allText);
            texts.add(allText);
        }
    }

    /**
     * Метод, собирающий полученные новости в csv файл.
     * Это завершающий метод по сбору данных, после записи новостей в csv файл, метод освобождает собранные данные в программе.
     * @param separator - символ разделения колонок.
     * @param fileName - имя файла.
     * @param value - признако достоверности сообщения.
     * @param type - категория сообщения.
     */
    private static void writeToCsvFile(String separator, String fileName, String value, String type){
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
            sb.append(separator);
            sb.append("Type");
            sb.append('\n');

            for(int i = 0; i < sites.size() - 1; ++i) {
                if(texts.get(i).contains(". INTERFAX.RU - ")) {
                    String text = texts.get(i).split(". INTERFAX.RU - ")[1];
                    sb.append(sites.get(i));
                    sb.append(separator);
                    sb.append(headings.get(i));
                    sb.append(separator);
                    sb.append(text);
                    sb.append(separator);
                    sb.append(value);
                    sb.append(separator);
                    sb.append(type);
                    sb.append('\n');
                }
            }

            writer.append(sb.toString());
            System.out.println("DONE!");
        } catch (IOException e) {
            e.printStackTrace();
        }
        sites.clear();
        headings.clear();
        texts.clear();
    }

    /**
     * Метод, собирающий все данные с сайта interfax.ru в 5 категориях.
     */
    private static void takeAllData(){
        try {
            for(int i = 1; i <= 30; ++i){
                takeNewsAddress("https://www.interfax.ru/business/news/2019/11/" + Integer.toString(i));
                writeToCsvFile(";", "interfax_business_" + Integer.toString(i) +"_11_2019.csv"
                        , "True", "Business");
            }
            for(int i = 1; i <= 30; ++i){
                takeNewsAddress("https://www.sport-interfax.ru/news/2019/11/" + Integer.toString(i));
                writeToCsvFile(";", "interfax_sport_" + Integer.toString(i) +"_11_2019.csv"
                        , "True", "Sport");
            }
            for(int i = 1; i <= 30; ++i){
                takeNewsAddress("https://www.interfax.ru/culture/news/2019/11/" + Integer.toString(i));
                writeToCsvFile(";", "interfax_culture_" + Integer.toString(i) +"_11_2019.csv"
                        , "True", "Culture");
            }
            for(int i = 1; i <= 30; ++i){
                takeNewsAddress("https://www.interfax.ru/world/news/2019/11/" + Integer.toString(i));
                writeToCsvFile(";", "interfax_world_" + Integer.toString(i) +"_11_2019.csv"
                        , "True", "World");
            }
            for(int i = 1; i <= 30; ++i){
                takeNewsAddress("https://www.interfax.ru/russia/news/2019/11/" + Integer.toString(i));
                writeToCsvFile(";", "interfax_russia_" + Integer.toString(i) +"_11_2019.csv"
                        , "True", "Russia");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод, удаляющий дубликаты из файла csv. Оставляется только та новость с "главным" (указанным в ссылке) типом.
     * @param fileName - имя файла.
     * @param separator - знак разделения столбцов.
     */
    private static void removeDuplicates(String fileName, String separator){
        List<List<String>> records = new ArrayList<>();

        //Считываем данные из файла в массив для работы
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "Cp1251"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[]values = line.split(separator);
                records.add(Arrays.asList(values));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<String> firstString = records.get(0);
        //Удаляем строку с заголовками
        records.remove(0);

        //Сортируем данные по значению первого столбца (ссылки)
        //records.sort((o1, o2) -> o1.get(0).compareTo(o2.get(0)));


        //Удаляем дубликаты
        for(int i = 0; i < records.size(); ++i){
            if(!records.get(i).get(0).contains(records.get(i).get(4).toLowerCase())) {
                records.remove(i);
                --i;
            }
        }

        try (OutputStreamWriter writer =
                     new OutputStreamWriter(new FileOutputStream(fileName), Charset.forName("cp1251"))) {
            StringBuilder sb = new StringBuilder();

            //Записываем в файл заголовки
            for(int i = 0; i < firstString.size(); ++i){
                sb.append(firstString.get(i));
                sb.append(separator);
            }

            sb.append('\n');

            //Записываем в файл информацию
            for (int i = 0; i < records.size(); ++i){
                for(int j = 0; j < records.get(i).size(); ++j){
                    sb.append(records.get(i).get(j));
                    sb.append(separator);
                }
                sb.append('\n');
            }

            writer.append(sb.toString());
            System.out.println("DONE!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
    }
}
