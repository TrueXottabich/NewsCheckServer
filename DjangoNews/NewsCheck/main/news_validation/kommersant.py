# Файл содержит обработку сайта https://www.kommersant.ru/

import re
import requests
from bs4 import BeautifulSoup


def is_kommersant(link):
    flag = False
    regex = re.compile('https://www.kommersant.ru/doc/\d+')

    if re.match(regex, link) is not None:
        flag = True

    return flag


def get_text_from_kommersant(link):
    r = requests.get(link)
    soup = BeautifulSoup(r.text, 'html.parser')
    text = soup.find('meta', itemprop='description').get('content')
    return text