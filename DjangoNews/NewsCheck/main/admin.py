# Файл содержит регистрацию моделей для административной панели
from django.contrib import admin
from .models import CheckModel, NewsModel, AskRightsModel

admin.site.register(CheckModel)
admin.site.register(NewsModel)
admin.site.register(AskRightsModel)
