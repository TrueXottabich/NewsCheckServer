import pickle
import joblib
import sklearn


# Функция для проверки новости
def check_news(text, models_name):
    try:
        loadedModel = pickle.load(models_name.file_1)
        count_vectorizer = joblib.load(models_name.file_2)
        text_array = [text]
        test_counts = count_vectorizer.transform(text_array)

        verdict = loadedModel.predict(test_counts)
        if verdict[0] == 0:
            proba = loadedModel.predict_proba(test_counts)[0][0]
            proba = float('{:.3}'.format(proba))
            return "Фейк. Вероятность: " + str(proba)
        elif verdict[0] == 1:
            proba = loadedModel.predict_proba(test_counts)[0][1]
            proba = float('{:.3}'.format(proba))
            return "Правда. Вероятность: " + str(proba)

    except IOError:
        return "Модель отсутствует"


# Функция для получения текста новости и отправки на проверку
def check_text(models_name, news_text):
    verdict = check_news(news_text, models_name)
    return verdict


# Функция для получения ссылки на новость и отправки на проверку
def check_link(models_name, news_link):
    return "Тут когда-нибудь будет проверка..."
