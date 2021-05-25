# Файл содержит декораторы проверки прав пользователей
from django.http import HttpResponse
from django.shortcuts import redirect


# Проверка на то, что пользователь не авторизован
def unauthenticated_user(view_func):
    def wrapper_func(request, *args, **kwargs):
        if request.user.is_authenticated:
            return redirect('home')
        else:
            return view_func(request, *args, **kwargs)

    return wrapper_func


# Проверка на то, что пользователь обладает указанной ролью
def allowed_users(allowed_roles=[]):
    def decorator(view_func):
        def wrapper_func(request, *args, **kwargs):
            group = None
            if request.user.groups.exists():
                group = request.user.groups.all()[0].name
            if group in allowed_roles:
                return view_func(request, *args, **kwargs)
            else:
                return HttpResponse("У вас недостаточно прав для просмотра этой страницы.")

        return wrapper_func

    return decorator
