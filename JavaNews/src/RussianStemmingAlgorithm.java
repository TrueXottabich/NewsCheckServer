import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Класс для реализации стемминга по алгоритму Мартина Портера:
 * http://snowball.tartarus.org/algorithms/russian/stemmer.html
 */
public class RussianStemmingAlgorithm {

    private static final Pattern PERFECTIVE_GERUND = Pattern.compile("((ив|ивши|ившись|ыв|ывши|ывшись)|((?<=[ая])(в|вши|вшись)))$");
    private static final Pattern ADJECTIVE = Pattern.compile("(ее|ие|ые|ое|ими|ыми|ей|ий|ый|ой|ем|им|ым|ом|его|ого|ему|ому|их|ых|ую|юю|ая|яя|ою|ею)$");
    private static final Pattern PARTICIPLE = Pattern.compile("((ивш|ывш|ующ)|((?<=[ая])(ем|нн|вш|ющ|щ)))$");
    private static final Pattern REFLEXIVE = Pattern.compile("(с[яь])$");
    private static final Pattern VERB = Pattern.compile("((ила|ыла|ена|ейте|уйте|ите|или|ыли|ей|уй|ил|ыл|им|ым|ен|ило|ыло|ено|ят|ует|уют|ит|ыт|ены|ить|ыть|ишь|ую|ю)|((?<=[ая])(ла|на|ете|йте|ли|й|л|ем|н|ло|но|ет|ют|ны|ть|ешь|нно)))$");
    private static final Pattern NOUN = Pattern.compile("(а|ев|ов|ие|ье|е|иями|ями|ами|еи|ии|и|ией|ей|ой|ий|й|иям|ям|ием|ем|ам|ом|о|у|ах|иях|ях|ы|ь|ию|ью|ю|ия|ья|я)$");
    private static final Pattern SUPERLATIVE = Pattern.compile("(ейше|ейш)$");
    private static final Pattern DERIVATIONAL = Pattern.compile(".*[^аеиоуыэюя]+[аеиоуыэюя].*ость?$");

    private static final Pattern RVRE = Pattern.compile("^(.*?[аеиоуыэюя])(.*)$");
    private static final Pattern DER = Pattern.compile("ость?$");

    private static final Pattern I = Pattern.compile("и$");
    private static final Pattern P = Pattern.compile("ь$");
    private static final Pattern NN = Pattern.compile("нн$");

    /**
     * Метод, выполняющий стемминг данных (выделяет в каждом слове его основу) по алгоритму Мартина Портера.
     * @param records - данные.
     * @return преобразованные данные.
     */
    public static void russianStemmingAlgorithmForData(List<List<String>> records){
        PreparingData.dataToLowerCase(records);
        PreparingData.removeAllNonLetters(records);

        //Не обрабатывает заголовки
        for(int i = 1; i < records.size(); ++i){
            //Обрабатываем только столбцы с текстами новостей
            for(int j = 2; j < 3; ++j) {
                String[] words = records.get(i).get(j).split(" ");

                for(int l = 0; l < words.length; ++l){
                    String word = words[l];
                    word = word.replace('ё', 'е');

                    //Выполняем алгоритм Мартина Портера
                    Matcher m = RVRE.matcher(word);
                    if(m.matches()) {
                        String pre = m.group(1);
                        String rv = m.group(2);
                        String temp = PERFECTIVE_GERUND.matcher(rv).replaceFirst("");
                        if (temp.equals(rv)) {
                            rv = REFLEXIVE.matcher(rv).replaceFirst("");
                            temp = ADJECTIVE.matcher(rv).replaceFirst("");
                            if (!temp.equals(rv)) {
                                rv = temp;
                                rv = PARTICIPLE.matcher(rv).replaceFirst("");
                            } else {
                                temp = VERB.matcher(rv).replaceFirst("");
                                if (temp.equals(rv)) {
                                    rv = NOUN.matcher(rv).replaceFirst("");
                                } else
                                    rv = temp;
                            }
                        } else
                            rv = temp;

                        rv = I.matcher(rv).replaceFirst("");

                        if (DERIVATIONAL.matcher(rv).matches())
                            rv = DER.matcher(rv).replaceFirst("");

                        temp = P.matcher(rv).replaceFirst("");
                        if (temp.equals(rv)) {
                            rv = SUPERLATIVE.matcher(rv).replaceFirst("");
                            rv = NN.matcher(rv).replaceFirst("н");
                        }else
                            rv = temp;

                        word = pre + rv;
                    }
                    words[l] = word;
                }

                //Возвращаем переделанный текст на место
                String text = "";
                for(int k = 0; k < words.length - 1; ++k){
                    text += words[k] + " ";
                }
                text += words[words.length - 1];
                records.get(i).set(j, text);
            }
        }

        System.out.println("The data implemented russian stemming algorithm (excluding headers)!");
    }

}
