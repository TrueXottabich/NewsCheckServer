from django.db import models
from django.contrib.auth import get_user_model
from django.contrib.auth.models import User


User._meta.get_field('email')._unique = True


class NewsModel(models.Model):
    title = models.CharField('Название модели', max_length=300)
    file_1 = models.FileField(upload_to='documents/%Y/%m/%d')
    file_2 = models.FileField(upload_to='documents/%Y/%m/%d')

    def __str__(self):
        return self.title

    class Meta:
        verbose_name = 'Модель'
        verbose_name_plural = 'Модели'


class Check(models.Model):
    methods = (
        ('0', 'Проверка текста'),
        ('1', 'Проверка ссылки'),
    )
    method = models.CharField('Текст или ссылка?', max_length=50, choices=methods, default='0')
    model = models.ForeignKey(NewsModel(), on_delete=models.CASCADE, null=True)
    title = models.CharField('Название проверки', max_length=300)
    text = models.TextField('Текст')
    user = models.ForeignKey(get_user_model(), on_delete=models.CASCADE, null=True)
    verdict = models.CharField('Вердикт', max_length=50, default='Не проверено')

    def __str__(self):
        return self.title

    class Meta:
        verbose_name = 'Проверка новости'
        verbose_name_plural = 'Проверки новостей'


class AskRightsModel(models.Model):
    user = models.ForeignKey(get_user_model(), on_delete=models.CASCADE, null=True)
    text = models.TextField('Текст')

    def __str__(self):
        return self.user.username

    class Meta:
        verbose_name = 'Запрос особых прав'
        verbose_name_plural = 'Запросы особых прав'
