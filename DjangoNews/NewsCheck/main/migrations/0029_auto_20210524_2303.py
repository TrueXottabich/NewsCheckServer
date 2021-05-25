# Generated by Django 3.1.7 on 2021-05-24 20:03

import django.core.validators
from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('main', '0028_auto_20210524_2009'),
    ]

    operations = [
        migrations.AlterField(
            model_name='newsmodel',
            name='file_model',
            field=models.FileField(upload_to='documents/%Y/%m/%d', validators=[django.core.validators.FileExtensionValidator(['pickle'])]),
        ),
    ]
