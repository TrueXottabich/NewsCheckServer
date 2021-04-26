from .models import Check, NewsModel
from django.forms import ModelForm, TextInput, Textarea, RadioSelect, FileField
from django import forms
from django.contrib.auth.models import User
from django.contrib.auth.forms import UserCreationForm


class NewsModelForm(ModelForm):
    class Meta:
        model = NewsModel
        fields = {"title", "file_1", "file_2"}
        widgets = {
            "title": TextInput(attrs={
                'placeholder': "Введите название модели",
                'class': "form-control"
            }),
        }


class CreateUserForm(UserCreationForm):
    class Meta:
        model = User
        fields = ["username", "email", "password1", "password2"]


class CheckForm(ModelForm):
    class Meta:
        model = Check
        fields = {"method", "model", "title", "text"}
        widgets = {
            "method": RadioSelect(attrs={
                'id': "id_radio"
            }),
            "model": TextInput(attrs={
                'placeholder': "Введите название модели",
                'class': "form-control"
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
