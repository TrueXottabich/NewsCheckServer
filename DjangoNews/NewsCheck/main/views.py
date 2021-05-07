from django.shortcuts import redirect

from .decorators import unauthenticated_user, allowed_users
from .news_validation import check_text
from .models import Check, NewsModel, AskRightsModel
from .forms import CheckForm, CreateUserForm, NewsModelForm, UpdateProfile, AskRightsForm

from django.contrib import messages
from django.contrib.auth import authenticate, login, logout
from django.contrib.auth.decorators import login_required
from django.contrib.auth.models import Group

from django.shortcuts import render
from django.views.generic import CreateView
from django.urls import reverse_lazy
from django.core.mail import send_mail


def index(request):
    return render(request, 'main/index.html')


@login_required(login_url='login')
def history(request):
    context = {
        'news': Check.objects.filter(user=request.user)
    }
    return render(request, 'main/history.html', context)


@unauthenticated_user
def registerPage(request):
    form = CreateUserForm()
    if request.method == 'POST':
        form = CreateUserForm(request.POST)
        if form.is_valid():
            user = form.save()
            username = form.cleaned_data.get('username')

            group = Group.objects.get(name='authorized_user')
            user.groups.add(group)

            messages.success(request, 'Аккаунт был создан для ' + username)
            return redirect('login')

    context = {'form': form}
    return render(request, 'accounts/register.html', context)


@unauthenticated_user
def loginPage(request):
    if request.method == 'POST':
        username = request.POST.get('username')
        password = request.POST.get('password')
        user = authenticate(request, username=username, password=password)

        if user is not None:
            login(request, user)
            return redirect('home')
        else:
            messages.info(request, "Некорректные имя пользователя или пароль")

    context = {}
    return render(request, 'accounts/login.html', context)


@login_required(login_url='login')
def logoutUser(request):
    logout(request)
    return redirect('login')


@login_required(login_url='login')
def userPage(request):
    context = {}
    return render(request, 'accounts/user.html', context)


@login_required(login_url='login')
@allowed_users(allowed_roles=['admin'])
def add_model(request):
    return render(request, 'main/add_model.html')


def news_check(request):
    error = ''
    answer = ''
    if request.method == 'POST':
        form = CheckForm(request.POST)
        if form.is_valid():
            obj = form.save(commit=False)
            obj.user = request.user
            answer = check_text(form.cleaned_data.get("model"), form.cleaned_data.get("text"))
            obj.verdict = answer
            obj.save()
        else:
            error = 'Форма не верна'

    form = CheckForm()

    role = "user"
    try:
        if 'admin' == request.user.groups.all()[0].name:
            role = "admin"
    except:
        role = "none"

    context = {
        'form': form,
        'error': error,
        'answer': answer,
        'role': role
    }
    return render(request, 'main/news_check.html', context)


@login_required(login_url='login')
@allowed_users(allowed_roles=['admin'])
def add_model(request):
    error = ''
    if request.method == 'POST':
        form = NewsModelForm(request.POST)
        if form.is_valid():
            form.save()
        else:
            error = 'Форма не верна'

    form = NewsModelForm()
    context = {
        'form': form,
        'error': error,
    }
    return render(request, 'main/add_model.html', context)


class AddModel(CreateView):
    model = NewsModel
    form_class = NewsModelForm
    template_name = 'main/add_model.html'
    success_url = reverse_lazy('home')


@login_required(login_url='login')  # Пользователь должен быть авторизован
# Обновление информации в профиле пользователя
def update_profile(request):
    # Сообщение пользователю о результатах заполнения формы
    message = ''

    if request.method == 'POST':
        form = UpdateProfile(request.POST, instance=request.user)

        if form.is_valid():
            form.save()
            first_name = str(request.user.first_name)
            last_name = str(request.user.last_name)

            if first_name != '' or last_name != '':
                message = 'Данные обновлены для ' + first_name + ' ' + last_name
            else:
                message = 'Данные обновлены для ' + str(request.user.username)
        else:
            message = 'Форма не верна'

    user = request.user

    form = UpdateProfile(initial={
        'username': user.username,
        'email': user.email,
        'first_name': user.first_name,
        'last_name': user.last_name,
    })

    role = "user"
    if 'admin' == request.user.groups.all()[0].name:
        role = "admin"

    context = {
        'form': form,
        'message': message,
        'role': role
    }

    return render(request, 'accounts/user.html', context)


@login_required(login_url='login')  # Пользователь должен быть авторизован
@allowed_users(allowed_roles=['authorized_user'])  # Пользователь должен быть без особых прав
# Заполнение формы для получения особых прав
def ask_rights(request):
    # Сообщение пользователю о результатах заполнения формы
    message = ''

    if request.method == 'POST':
        form = AskRightsForm(request.POST)

        if form.is_valid():
            obj = form.save(commit=False)
            user = request.user
            obj.user = user

            # Блок отправки письма администратору
            try:
                login = str(user.username)
                # Заголовок письма администратору
                title = 'Пользователь ' + login + '. Получение особых прав.'
                # Текст письма администратору
                data = 'Пользователь ' + login + ' отправил запрос на получение особых прав с текстом:\n'
                data += str(form.cleaned_data.get("text")) + '\n'
                data += 'Почта пользователя: ' + str(user.email) + '\n'
                data += 'Имя и фамилия пользователя: ' + str(user.first_name) + ' ' + str(user.last_name)
                # Отправка письма администратору
                send_mail(title, data, "Yasoob", ['news.check.information@gmail.com'], fail_silently=False)

                obj.save()
                message = "Ваш запрос успешно отправлен и будет рассмотрен в ближайшее время"
            except:
                message = "Возникли проблемы с отправкой письма администратору. Пожалуйста, попробуйте позже"

        else:
            message = 'Форма не верна'

    form = AskRightsForm()

    # Нужно ли показывать форму? Если пользователь уже отправлял запрос - нет
    need = True
    if AskRightsModel.objects.filter(user=request.user).exists():
        need = False

    context = {
        'form': form,
        'message': message,
        'need': need
    }

    return render(request, 'accounts/ask_rights_form.html', context)
