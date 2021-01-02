# Autopoet
Poem generator in Russian / Генератор стихотворений на русском языке

## English

### Working principle
Autopoet generates poems based on rhythmic structure and rhyming scheme the user defines. Until the poem reaches the desired length, the algorithm generates syntax structures automatically or picks them from a dedicated database. The syntax structures include grammatical characteristics of words and punctuation. Each 'slot' of such structure is populated with words from a dictionary that match not only the grammatical characteristics but also stress and evetually rhymes. If a sentence cannot be composed using words in the dictionary, the algorithm retries to generate it.

New word input is manual for precise stress input. This process is assisted where possible using a morphological dictionary of Russian language.

### How to launch?
Files from this repository should be put in a package `autopoet` and then imported from the file system in Eclipse or other Java IDE.

### Disclaimer
Since this is my first side project I was doing just for fun parallel to my mechanical engineering studies, it contains LOADS of boilerplate code and extensive usage of Ctrl+C Ctrl+V, which is BAD! However, the program works and generates crazy poems pretty fast (see examples at the bottom).

## Русский

### Принцип работы
Данная программа создаёт стихотворения на основе размера и схемы рифмовки, которые задаёт пользователь. Пока стихотворение не дописано до конца, алгоритмом создаётся синтаксическая структура или выбирается случайно из базы сохранённых синтаксических структур. Такая структура включает в себя последовательность слов и знаков препинания со всеми необходимыми грамматическими признаками. Далее, к каждой ячейке структуры подбираются слова из словаря, которые подходят не только по грамматическим признакам, но и по ударениям, а в случая окончания строки - также по рифме. Если предложение не удаётся дописать до конца, оно стирается из памяти, заново подбирается синтакисческая структура, и процесс повторяется.

Ввод новых слов происходит вручную, так как требуется высокая точность в разметке ударений. Ввод форм прилагательных автоматизирован так, что пользователю не придётся вводить все 24 формы прилагательного. Частично используется полная парадигма русского языка (текстовый файл morph, который не был загружен здесь).

### Как запустить?
Программу можно скомпилировать в Eclipse Photon, поместив исходный код в пакет autopoet, и использовав экспорт проектов из файловой системы. Также потребуется текстовый файл morph, который можно получить, связавшись с создателем.

### Дисклеймер
Это мой первый "интересный" проект, поэтому код крайне непродуман, и был написан с чрезмерным использованием Ctrl+C Ctrl+V. Тем не менее, эта программа работает, и создаёт стихотворение на 12 строк буквально за 2-3 секунды.

## Generated examples / Примеры произведений

***
остывает театр стакана

.выпивает окно молодым

кокаином .безумная башня

вобралась в дурака .головы


демократ переломлен препросто

;догорает Навальный лица

.лабиринты ,которыми громко

просекут ветрогонку .леса


-философская армия .ходит

ледоспуск в окаянстве .моё

наслаждение обдано .хочет

одиночество алым дождём


.излучать подчеканку .болеет

оправдание .высидев есть

человек .безуспешно тускнеет

гражданин оправданий .


***
и повымерло стекло любви

,чувство злой двери .в планете ездил

воздух .я не заменяю земли

.где таинственный Навальный бил

нового Навального ,болеет


лестница .театры облаков

размышляют ,если я плеснувши

жрёт ?сейчас гниёт джедай ?ворую

связь .земля летела .головой

скучной есть глаза .дороги путник


съеден очень ;здание -стола

армия .туманный мёртвый вечер

юный заменяет сон .болеет

снег !бредёт Навальный .хороша

и полна душа их .я болею


на простой строке .гротеск бежал

.мой китаец на китайце кинул

.чай слезы ,проект хороший !ветер

закрывая остывает .смерть

верила .строка не ходит .


***
безуспешно уходит Навальный

наслаждения .ездят грехи

.размышляет окно ...как Навальный

догорает ,врагов продавив


;красота отдыхала .уходят

лабиринты .счастливый асфальт

поместился любовью .мороза

философские сны исчерпав


***
дожди слезы в моих дворах

.стакан томился .дурачок

печали ,день хороший !враг

остыл .легко бежало дно


.колено -лишь Навальный .сном

теряет тихая судьба

.душа не размышляет .что

такое связь ?

***
ненавижу клавишу .слепое

будит светлая душа .дорожки

над грехом болеют !ледоспуск

музыки ,арбуз мгновенный !море

цвета переярясь долбит .в_общем

размышляет на пустом глазу


словом первым сон .кричать однажды

.ною ,есть в прозрачном деле .камни

выпивают двор .теперь даёт

мир .тускнеют вещи .смехом знает

милая пустыня .плыть штукарски

.так вербовка не готова .шлёт


вещь стакан стекла ?потом ворует

воздух пыли .день безумный !путник

мысли города в асфальте ,рай

или верный самолёт .арбузы

и красоты нас сосут .чугунных

крылья бьют компьютерами .жрать


музыку .обмахнутое любит

гадившаяся звезда .прозрачный

рай ,ночной огонь ,двери пустой

мрачная бессонница .прозрачных

небеса теряют снами .башни

ноют .лучше спать в дороге .


***
морозом поёт молодая луна

.какая холодная музыка !план

слепой с разыгравшими стульями !враг


томился .земля отдыхала .живут

смешки ,остывают дороги .плывут

дожди ,остывают ошибки .бредут


моря ,остывают китайцы .они

подумают .хочет траву гражданин

.она хороша .безуспешно звенит


китаец .долблю красоту .молодой

тоталитаризм заболел головой

.леса остывают .
