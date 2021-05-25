# Файл содержит определение моделей
from django.db import models
from django.contrib.auth import get_user_model
from django.contrib.auth.models import User
from django.core.validators import FileExtensionValidator


User._meta.get_field('email')._unique = True


# Модель алгоритмов классификации
class NewsModel(models.Model):
    title = models.CharField('Название модели', max_length=300)
    file_model = models.FileField(upload_to='documents/%Y/%m/%d', validators=[FileExtensionValidator(['pickle'])])
    file_vector = models.FileField(upload_to='documents/%Y/%m/%d', validators=[FileExtensionValidator(['pkl'])])

    def __str__(self):
        return self.title

    class Meta:
        verbose_name = 'Модель'
        verbose_name_plural = 'Модели'


# Модель проверки новостей
class CheckModel(models.Model):
    methods = (
        ('0', 'Проверка текста'),
        ('1', 'Проверка ссылки'),
    )
    method = models.CharField('Текст или ссылка?', max_length=50, choices=methods, default='0')
    model = models.ForeignKey(NewsModel(), on_delete=models.CASCADE, null=True)
    title = models.CharField('Название проверки', max_length=300)
    text = models.TextField('Текст')
    user = models.ForeignKey(get_user_model(), on_delete=models.CASCADE, blank=True, null=True)
    verdict = models.CharField('Вердикт', max_length=50, default='Не проверено')

    def __str__(self):
        return self.title

    class Meta:
        verbose_name = 'Проверка новости'
        verbose_name_plural = 'Проверки новостей'


# Модель запросов прав администратора
class AskRightsModel(models.Model):
    user = models.ForeignKey(get_user_model(), on_delete=models.CASCADE, null=True)
    text = models.TextField('Текст')

    def __str__(self):
        return self.user.username

    class Meta:
        verbose_name = 'Запрос особых прав'
        verbose_name_plural = 'Запросы особых прав'
