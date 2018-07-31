## Файлы стилей.

#### Редактирование стилей

Все таблицы стилей, относящиеся к инфраструктуре находятся в модуле `ops-inf-ui` в каталоге `less` и должны правится только там. 

Структура динамических стилей выглядит следующим образом:

    /less 
    
        /components             // Глобально переопределяются стили для каждого конкретного компонента  

        /layout                 // Стили, относящиеся к разметке 

        /mixins                 // Миксины

        /modena-layout          // Копия оригинального файла modena-layout. Его править не нужно

        /modena-theme           // Копия оригинального файла theme. Его править не нужно

        /utilities              // Стили, содержащие вспомогательные классы (floats, widths, alignments etc.)

        /variables              // Переменные и их значения добавлять и править в файле ops-inf-variables. Переменные layout и theme править не нужно 

        /view                   // Здесь правятся стили относящиеся к конкретной вью. Глобального оверрайта стилей компонентов здесь быть не должно
        ops-inf.less            // Сюда подключаются стили из вышеописанных каталогов, кроме modena-layout и theme
        
          
В `ops-inf.less` происходит подключение файлов через **@import**: 

    @import "components/breadcrumbs"

Все стили для правки находятся в подкаталогах, например, в `components` и т.п.

#### Переопределение стилей
Если нужно изменить внешний вид компонента в рамках всего приложения, значит нужно переопределить стили глобально. Глобальное переопределение стилей (modena и primefaces) должно происходить в блоке components.  
 
#### Создать новую таблицу стилей
Если таблицы стилей должна описывать стили для конкретной view или frame - называние берется по имени соответствующей view, если это стили для компонента ко всему приложению - берется название компонента.
Названия таблиц стилей начинаются со строчной буквы, в названиях может быть использован дефис. 
Новая таблица подключается через **@import** в соответствующем блоке в `ops-inf.less`

#### Имена стилей
Давая названия новым селекторам (id, class) не нужно использовать префиксы **ui-** (префикс jQuery UI библиотеки, применяемой в primefaces) или **m-** (префикс modena библиотеки). 



#### Компиляция стилей
Все стили, содержащиеся в подкаталогах компилируются и конкатенируются в итоговый `ops-inf.css`, который кладется в `/resourсes/ops-inf/styles`.
Modena-layout и theme так же компилируются и кладутся в соответствующие каталоги `/resourсes/ops-inf/modena-layout` и `/resourсes/ops-inf/modena-theme`, затем подключаются в проект во View.xhtml.

## Статические ресурсы

В `/resourсes/ops-inf` находятся 
 - картинки 
 - скрипты
 - шрифты
 - таблицы стилей сторонних библиотек
 - скомпилированные таблицы стилей
 