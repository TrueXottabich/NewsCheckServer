# Generated by Django 3.1.7 on 2021-04-21 13:45

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('main', '0017_auto_20210419_1915'),
    ]

    operations = [
        migrations.AlterField(
            model_name='newsmodel',
            name='file_2',
            field=models.FileField(upload_to='documents/%Y/%m/%d'),
        ),
        migrations.AlterField(
            model_name='newsmodel',
            name='title',
            field=models.CharField(max_length=300, verbose_name='Название модели'),
        ),
    ]