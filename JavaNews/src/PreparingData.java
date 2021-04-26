import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Подготавливает сырые данные для последующего обучения на них моделей.
 */
public class PreparingData {

    /**
     * Метод, считывающий даные из файла формата csv.
     * @param fileName - название файла с данными.
     * @param separator - знак, которым разделяются столбцы в csv файле.
     * @return считанные даные.
     */
    public static List<List<String>> readData(String fileName, String separator){
        List<List<String>> records = new ArrayList<>();

        //Считываем данные из файла в массив для работы
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "Cp1251"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[]values = line.split(separator);
                records.add(Arrays.asList(values));
            }
        } catch (Exception e) {
            System.out.println("Cannot read data correctly!");
        }

        System.out.println("The data was read from the file " + fileName + " correctly!");

        return records;
    }

    /**
     * Метод, записывающий данные в csv файл.
     * @param records - данные, которые необходимо записать в файл.
     * @param fileName - название файла, в который нужно записать данные.
     * @param separator - знак, которым разделяются столбцы в csv файле.
     */
    public static void writeData(List<List<String>> records, String fileName, String separator){
        try (OutputStreamWriter writer =
                     new OutputStreamWriter(new FileOutputStream(fileName), Charset.forName("cp1251"))) {
            StringBuilder sb = new StringBuilder();

            //Записываем в файл информацию
            for (int i = 0; i < records.size(); ++i){
                for(int j = 0; j < records.get(i).size(); ++j){
                    sb.append(records.get(i).get(j));
                    sb.append(separator);
                }
                sb.append('\n');
            }

            writer.append(sb.toString());

        } catch (IOException e) {
            System.out.println("Cannot write data correctly!");
        }

        System.out.println("Data was written to a file " + fileName + " correctly!");
        System.out.println();
    }

    /**
     * Метод, заменяющий во всех текстах новостей заглавные буквы на строчные.
     * @param records - данные, в которых надо произвести изменения.
     */
    public static void dataToLowerCase(List<List<String>> records){
        //Не обрабатывает заголовки
        for(int i = 1; i < records.size(); ++i)
            for(int j = 2; j < 3; ++j)
                records.get(i).set(j, records.get(i).get(j).toLowerCase());

        System.out.println("All uppercase letters in the data have been replaced with lowercase (excluding headers)!");
    }

    /**
     * Метод, удаляющий во всех текстах новостей все символы, кроме пробелов и русских букв.
     * @param records - данные, в которых надо произвести изменения.
     */
    public static void removeAllNonLetters(List<List<String>> records){
        //Не обрабатывает заголовки
        for(int i = 1; i < records.size(); ++i)
            //Обрабатываем только столбцы с текстами новостей
            for(int j = 2; j < 3; ++j) {
                //Заменяем один или несколько подряд идущих невидимых символа на пробел
                records.get(i).set(j, records.get(i).get(j).replaceAll("[ \t\n\r]+", " "));

                //Удаляем все не буквы и не пробелы
                records.get(i).set(j, records.get(i).get(j).replaceAll("[^а-яА-Я ]", ""));
            }

        System.out.println("Only russian letters and spaces remained in the data (excluding headers)!");
    }

    /**
     * Метод, удаляющий во всех текстах новстей слова, встречающиеся в указаннном csv файле.
     * @param records - данные, в которых надо произвести изменения.
     * @param wordsFileName - название файла со словами, в котором находятся слова, которые необходимо удалить из данных.
     * @param separator - знак, которым разделяются столбцы в csv файле.
     */
    public static void removeStopWords(List<List<String>> records, String wordsFileName, String separator){
        List<List<String>> stopWords = readData(wordsFileName, separator);

        //Не обрабатывает заголовки
        for(int i = 1; i < records.size(); ++i)
            //Обрабатываем только столбцы с текстами новостей
            for (int j = 2; j < 3; ++j)
            {
                String[] wordsFromLine = records.get(i).get(j).split(" ");
                //Удаляем пустые слова
                wordsFromLine = Arrays.stream(wordsFromLine).filter(word -> !word.equals("")).toArray(String[]::new);

                //Проверка на все стоп слова
                for (List<String> stopWordArray : stopWords)
                {
                    String stopWord = stopWordArray.get(0);
                    wordsFromLine = Arrays.stream(wordsFromLine).filter(word -> !word.equals(stopWord)).toArray(String[]::new);
                }
                //Сохраняем исправленную строку
                records.get(i).set(j, String.join(" ", wordsFromLine));
            }

        System.out.println("All stop words removed from the data (excluding headers)!");
    }

    /**
     * Метод, удаляющий во всех текстах новстей слова, встречающиеся в данных в единственном экземпляре.
     * @param records - данные, в которых надо произвести изменения.
     */
    public static void removeSingleWords(List<List<String>> records) {
        Map<String, Integer> allWords = new HashMap<>();
        //Обрабатывает заголовки
        for(int i = 1; i < records.size(); ++i)
            //Обрабатываем только столбцы с текстами новостей
            for (int j = 2; j < 3; ++j) {
                String[] wordsFromLine = records.get(i).get(j).split(" ");
                for (String word:wordsFromLine) {
                    Integer integer = new Integer(1);
                    if (allWords.containsKey(word))
                        integer = new Integer(allWords.get(word).intValue() + 1);
                    allWords.put(word, integer);
                }
            }

        //этап 2
        allWords = allWords.entrySet().stream()
                .filter(x -> x.getValue() == 1)
                .collect(Collectors.toMap(y -> y.getKey(), z -> z.getValue()));

        //этап 3
        final Set<String> keySet = allWords.keySet();
        //Не обрабатывает заголовки
        for(int i = 1; i < records.size(); ++i)
            //Обрабатываем только столбцы с текстами новостей
            for (int j = 2; j < 3; ++j) {
                String[] wordsFromLine = records.get(i).get(j).split(" ");

                //Проверка на слова встречающиеся 1 раз
                wordsFromLine = Arrays.stream(wordsFromLine).filter(word -> !keySet.contains(word)).toArray(String[]::new);

                //Удаляем пустые слова
                wordsFromLine = Arrays.stream(wordsFromLine).filter(word -> !word.equals("")).toArray(String[]::new);

                //Сохраняем исправленную строку
                records.get(i).set(j, String.join(" ", wordsFromLine));
            }

        System.out.println("All single words removed from the data (excluding headers)!");
    }

    /**
     * Метод, формирующий из данных мешок слов.
     * @param records - данные, из которых необходимо сформировать мешок слов.
     * @return полученный мешок слов.
     */
    public static List<List<String>> createBagOfWords(List<List<String>> records)
    {
        Set<String> allWordsSet = new HashSet<>();
        //Обрабатывает заголовки
        for(int i = 1; i < records.size(); ++i)
            //Обрабатываем только столбцы с текстами новостей
            for (int j = 2; j < 3; ++j) {
                Collections.addAll(allWordsSet, records.get(i).get(j).split(" "));
            }
        //удалим пустое слово
        allWordsSet.remove("");
        //записываем в массив (чтобы были стабильные индексы)
        String[] allWordsArray = new String[allWordsSet.size()];
        allWordsSet.toArray(allWordsArray);

        List<List<String>> bagOfWords = new ArrayList<>();

        //Заголовки
        ArrayList<String> backOfWordsHeaders = new ArrayList<>();
        backOfWordsHeaders.add(records.get(0).get(0)); //ссылка на сайт
        backOfWordsHeaders.add(records.get(0).get(1)); //заголовок
        backOfWordsHeaders.add(records.get(0).get(3)); //значение
        backOfWordsHeaders.add(records.get(0).get(4)); //тип
        //Сами слова в заголовке
        backOfWordsHeaders.addAll(Arrays.asList(allWordsArray));
        bagOfWords.add(backOfWordsHeaders);

        //Не обрабатывает заголовки
        for(int i = 1; i < records.size(); ++i)
        {
            ArrayList<String> backOfWord = new ArrayList<>(3 + allWordsArray.length);
            backOfWord.add(records.get(i).get(0)); //ссылка на сайт
            backOfWord.add(records.get(i).get(1)); //заголовок
            backOfWord.add(records.get(i).get(3)); //значение
            backOfWord.add(records.get(i).get(4));  //тип
            //начинаются слова
            Map<String, Integer> wordsFromTextMap = new HashMap<>();
            String[] wordsFromTextArray = records.get(i).get(2).split(" ");
            for (String word : wordsFromTextArray) {
                Integer integer = new Integer(1);
                if (wordsFromTextMap.containsKey(word))
                    integer = new Integer(wordsFromTextMap.get(word).intValue() + 1);
                wordsFromTextMap.put(word, integer);
            }
            for (int k = 0; k<allWordsArray.length; k++)
            {
                if (wordsFromTextMap.containsKey(allWordsArray[k]))
                    backOfWord.add(wordsFromTextMap.get(allWordsArray[k]).toString());
                else
                    backOfWord.add("0");
            }
            bagOfWords.add(backOfWord);
        }

        System.out.println("Bag of words was created correctly!");
        return bagOfWords;
    }

    public static void main(String[] args)
    {
        List<List<String>> records = readData("data_version_00 (5645).csv", ";");

        //Шаг 1: преобразуем все заглавные буквы в строчные
        dataToLowerCase(records);
        writeData(records, "step1.csv", ";");

        //Шаг 2: оставляем в тексте только буквы и пробельные символы
        removeAllNonLetters(records);
        writeData(records, "step2.csv", ";");

        //Предполагаем, что новостные издания не допускают написания слов с ошибками (поэтому пропускаем шаг по устранению ошибок)

        //Шаг 3: удаляем из данных стоп-слова
        removeStopWords(records, "StopWords.csv", ",");
        writeData(records, "step3.csv", ";");

        //Шаг 4: стемминг, алгоритм Портера для русского языка - выделение основы слов
        RussianStemmingAlgorithm.russianStemmingAlgorithmForData(records);
        writeData(records, "step4.csv", ";");

        //Шаг 5: удаляем из данных нерелевантные слова
        removeSingleWords(records);
        writeData(records, "step5.csv", ";");

        //Шаг 6: собираем из данных мешок слов
        List<List<String>> bagOfWords = createBagOfWords(records);
        writeData(bagOfWords, "step6.csv", ";");
    }
}