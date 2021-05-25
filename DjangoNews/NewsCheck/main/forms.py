# Файл содержит определение форм
from .models import CheckModel, NewsModel, AskRightsModel
from django.forms import ModelForm, TextInput, Textarea, RadioSelect, EmailInput, PasswordInput
from django import forms
from django.contrib.auth.models import User
from django.contrib.auth.forms import UserCreationForm


# Форма истории проверки новостей
class NewsModelForm(ModelForm):
    class Meta:
        model = NewsModel
        fields = {"title", "file_model", "file_vector"}
        widgets = {
            "title": TextInput(attrs={
                'placeholder': "Введите название модели",
                'class': "form-control"
            }),
        }


# Форма пользователя
class CreateUserForm(UserCreationForm):
    class Meta:
        model = User
        fields = ["username", "email", "password1", "password2"]
        widgets = {
            "username": TextInput(attrs={
                'placeholder': "Введите логин",
                'class': "form-control"
            }),
            "email": EmailInput(attrs={
                'placeholder': "Введите адрес электронной почты",
                'class': "form-control"
            }),
            "password1": PasswordInput(attrs={
                'placeholder': "Введите пароль",
                'class': "form-control"
            }),
            "password2": PasswordInput(attrs={
                'placeholder': "Повторите пароль",
                'class': "form-control"
            })
        }

    def __init__(self, *args, **kwargs):
        super(CreateUserForm, self).__init__(*args, **kwargs)

        self.fields['email'].required = True


# Форма профиля пользователя
class UpdateProfile(forms.ModelForm):
    class Meta:
        model = User
        fields = ('username', 'email', 'first_name', 'last_name')
        widgets = {
            "username": TextInput(attrs={
                'placeholder': "Введите логин",
                'class': "form-control",
                'readonly': 'readonly'
            }),
            "email": TextInput(attrs={
                'placeholder': "Введите адрес электронной почты",
                'class': "form-control",
                'readonly': 'readonly'
            }),
            "first_name": TextInput(attrs={
                'placeholder': "Введите имя",
                'class': "form-control"
            }),
            "last_name": TextInput(attrs={
                'placeholder': "Введите фамилию",
                'class': "form-control"
            }),
        }


# Форма проверки новости
class CheckForm(ModelForm):
    class Meta:
        model = CheckModel
        fields = {"method", "model", "title", "text"}
        widgets = {
            "method": RadioSelect(attrs={
                'id': "id_radio"
            }),
            "title": TextInput(attrs={
                'placeholder': "Введите название проверки для сохранения",
                'class': "form-control"
            }),
            "text": Textarea(attrs={
                'placeholder': "Введите новость",
                'class': "form-control",
                'id': "id_text"
            }),
        }


# Форма добавления модели
class PostForm(ModelForm):
    class Meta:
        model = NewsModel
        fields = ['title', 'file_model', 'file_vector']


# Форма запроса прав администратора
class AskRightsForm(ModelForm):
    class Meta:
        model = AskRightsModel
        fields = ["text"]
        widgets = {
            "text": Textarea(attrs={
                'placeholder': "Введите обращение",
                'class': "form-control",
                'id': "id_text"
            })
        }

