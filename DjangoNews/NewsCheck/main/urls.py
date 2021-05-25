# Файл содержит определение ссылок приложения
from django.urls import path
from django.contrib.auth.decorators import login_required
from django.contrib.auth import views as auth_views
from .decorators import allowed_users
from . import views  # Из текущей директории импортируем файл views

urlpatterns = [
    path('', views.index, name='home'),  # При переходе на главную страницу вызываем приложение main

    path('register', views.registerPage, name='register'),
    path('login', views.loginPage, name='login'),
    path('logout', views.logoutUser, name='logout'),
    path('confirm_email', views.confirmEmail, name='confirm_email'),
    path('user', views.update_profile, name='user'),

    path('news_check', views.news_check, name='news_check'),
    path('history', views.history, name='history'),
    path('add_model', login_required(login_url='login')(allowed_users(allowed_roles=['admin'])(views.AddModel.as_view())), name='add_model'),

    # Форма для ввода адреса электронной почты во время сброса пароля
    path('reset_password/', auth_views.PasswordResetView.as_view(template_name="accounts/reset_password.html"),
         name="reset_password"),
    # Информационное сообщение о том, что адреса электронной почты для сброса пароля верен
    path('reset_password_sent/', auth_views.PasswordResetDoneView.as_view(template_name="accounts/reset_password_sent.html"),
         name="password_reset_done"),
    # Ссылка для сброса пароля на почте
    path('reset/<uidb64>/<token>/', auth_views.PasswordResetConfirmView.as_view(template_name="accounts/reset_password_form.html"),
         name="password_reset_confirm"),
    # Информационное сообщение о том, что пароль был успешно изменен
    path('reset_password_complete/', auth_views.PasswordResetCompleteView.as_view(template_name="accounts/reset_password_complete.html"),
         name="password_reset_complete"),

    # Запрос прав на добавление моделей
    path('ask_rights', views.ask_rights, name='ask_rights')
]
