# Finance Tracker
This Android application is for tracking user's finances made by 
**Mikhail Pilipov**


**Description:**


The Finance Tracker application helps the user to track his or her money incomes and

expenses in convenient way.


**Key elements:**


Java, Android SDK, Objected-Oriented Programming, Data parcing, Layouts, Graphics,


Android RunTime ART, Menus, Events, Intents, Event Handlers, Activities, Lists, Arrays,

Internal Storage, Menus, Vector Assets, Navigators, Fragments, Piecharts.


**The functionality of the application:**


In this Android mobile application user is allowed to record his/her expenses and income,

to determine its balance, to get a graphical representation of it. All the records are being

saved into Internal storage of the Android device. Also, it is possible to convert the records


into Excel format file for later processing of it and to delete recordings that were made

before.


# **Home screen and Menu**

**Header:**


The application header represents a drop-down ‘Burger’ menu


**Main Menu Options:**


There is a small graphic label with the name of the application and the menu items which

are described below.


Menu items are presented with five options:


1) Home – the main screen of the application where it’s possible to track and to add

new expenses and income

2) Balance – represents a table with all the incomes and expenses, also with balance


between income and expenses

3) About – a brief info about the author of this application, also it’s possible to send an

email to him.

4) Settings – it’s possible to erase all the recordings of expenses and income

5) Export – this menu item is designed to export the data to Excel format file.



<img width="444" height="423" alt="image" src="https://github.com/user-attachments/assets/628aaa79-51a0-4ce3-86c3-f5769e28e8c5" />

Unfortunately, it was not possible to make the main screen more interesting, because the

side menu activity did not allow to put any other elements on it – the application stopped

working in such cases.

# **Expenses**

On this screen it is possible to look at all the current expenses and to add the new one.


In order to do it, it’s necessary to input amount of the new expense, to select category of it

and to tap the button ‘ADD’. After tapping this button, the new expense will be added to the

Piechart immediately. I used Piechart from the library com.github.mikephil.charting.


There are 9 possible categories of expenses: Health, Leisure, Home, Groceries, Gifts, Café,

Transport, Cosmetics, Clothes and Shoes.

<img width="548" height="343" alt="image" src="https://github.com/user-attachments/assets/99daf452-bd8a-4925-82a7-668cdaad35f8" />

In order to save all the recordings, I use the file “expenses.txt” in the Internal storage. Every


time when it is necessary to add new record, I load all the recordings from this file into

HashMap<String, Integer> structure, then I add the new record and then I save it into


Internal storage again.


Also, on every recording the application makes the recording into the file ‘transaction.json’,


where all the recordings are being saved. It is necessary to represent all of them in the one

table and to count the balance between income and expenses.


# **Income**

On this screen it is possible to look at all the current income and to add the new one.


In order to do it, it’s necessary to input amount of the new income, to select category of it

and to tap the button ‘ADD’. After tapping this button, the new income will be added to the

Piechart immediately. I used Piechart from the library com.github.mikephil.charting.


There are 5 possible categories of expenses: Paycheck, Gift, Dividend, Bonus, Cashback
<img width="672" height="418" alt="image" src="https://github.com/user-attachments/assets/d88e4d78-54fc-4e69-ba35-c4bfcfbd5d1d" />


In order to save all the recordings, I use the file “incomes.txt” in the Internal storage. Every

time when it is necessary to add new record, I load all the recordings from this file into

HashMap<String, Integer> structure, then I add the new record and then I save it into

Internal storage again.


Also, on every recording the application makes the recording into the file ‘transaction.json’,

where all the recordings are being saved. It is necessary to represent all of them in the one

table and to count the balance between income and expenses.


# **Balance**

There is a scrollable table (TableView) with all the expenses and income that were added before

with following information:


  - The kind of the record (expense or income)

  - The date when the record was made

  - The category of the expense or income

  - The amount of the record


On the bottom of this activity there is a second table with only one row with total balance


between expenses and incomes. If the balance is positive, the background of it is green.


Otherwise the background is red.

<img width="600" height="557" alt="image" src="https://github.com/user-attachments/assets/4cd359a1-10e1-4b94-9c60-3b9de9fd2a43" />


# **Settings**

In this Activity it’s possible to delete all the records about income and expenses and to return to

the Home Activity.

<img width="204" height="410" alt="image" src="https://github.com/user-attachments/assets/c0cf762b-0d85-4e6a-bd91-6dcd7533f2ac" />

# **Export**

In this activity it’s possible to make export all the data into Excel file format. I used Apache POI


library to do it. On the second screenshot you can see an example of exported data into Excel file.

<img width="500" height="490" alt="image" src="https://github.com/user-attachments/assets/c69f3fdf-b63d-4b61-bd41-97ca0584192c" />


# **About**

On this activity there is a short information about the author of this application and also a

hyperlink which allows to send an email to the author of this application.

<img width="265" height="500" alt="image" src="https://github.com/user-attachments/assets/64599d35-0ede-4614-abfd-8e6dd03f58d3" />

# **Conclusion**


The process of developing of this application was quite difficult, because a lot of different

technologies were used. Also, the design elements required to spend a lot of time to it (around


30-40% of time), especially the menus and the piecharts. Anyway, the Internet has a big

knowledge base which is helpful to write android applications. Also I applied a lot of knowledge


I received on Mobile Development classes, they were very helpful for me in the process of

developing my application.


