# Generated by Django 3.1.7 on 2021-04-19 16:15

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('main', '0016_auto_20210419_1914'),
    ]

    operations = [
        migrations.AlterField(
            model_name='newsmodel',
            name='file_1',
            field=models.FileField(upload_to='documents/%Y/%m/%d'),
        ),
    ]
