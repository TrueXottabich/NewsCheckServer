# Файл содержит проверку новостей по тексту или ссылке
import pickle
import joblib
import sklearn

from .kommersant import is_kommersant, get_text_from_kommersant


# Функция для проверки новости
def check_news(text, models_name):
    flag = False
    try:
        loadedModel = pickle.load(models_name.file_model)
        count_vectorizer = joblib.load(models_name.file_vector)
        text_array = [text]
        test_counts = count_vectorizer.transform(text_array)

        verdict = loadedModel.predict(test_counts)

        if verdict[0] == 0:
            proba = loadedModel.predict_proba(test_counts)[0][0]
            proba = float('{:.3}'.format(proba))
            flag = True
            ans = "Фейк. Вероятность: " + str(proba)
            return ans, flag
        elif verdict[0] == 1:
            proba = loadedModel.predict_proba(test_counts)[0][1]
            proba = float('{:.3}'.format(proba))
            flag = True
            ans = "Правда. Вероятность: " + str(proba)
            return ans, flag
    except:
        ans = "Возикли проблемы с моделью"
        return ans, flag


# Функция для получения текста новости и отправки на проверку
def check_text(models_name, news_text):
    verdict, flag = check_news(news_text, models_name)
    return verdict, flag


def which_portal(link):
    portal = ''
    if is_kommersant(link):
        portal = 'kommersant'
    return portal


# Функция для получения ссылки на новость и отправки на проверку
def check_link(models_name, news_link):
    news_portals = ['https://www.kommersant.ru/']
    portal = which_portal(news_link)

    flag = False
    text = ''
    if portal == '':
        verdict = "Ссылка не верна. На данный момент доступна проверка следующих новостных ресурсов:\n"
        for portal in news_portals:
            verdict += portal + '\n'
        return verdict, text, flag
    try:
        if portal == 'kommersant':
            text = get_text_from_kommersant(news_link)
            flag = True
    except:
        verdict = "Ссылка не может быть обработана.\n"
        return verdict, text

    verdict, flag = check_news(text, models_name)
    return verdict, text, flag
