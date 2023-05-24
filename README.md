# Project6Lab
Application to divide expenses between a group of users. Project 6 of Lab_Prog

Neste projeto iremos trabalhar com as classes Expense, ExpensesGroup, User
e UserManager.

A classe User representa um utilizador do sistema e guarda informação sobre o nome
de utilizador, grupos onde está envolvido, bem como o seu saldo global.
A classe UserManager representa um catálogo de User e tem operações que
permitem encontrar um determinado User registado no sistema pelo nome de
utilizador.

A classe Expense representa o conceito de Despesa e contém informação sobre o
descritivo da despesa, o seu valor, e as partes do valor associadas a cada um dos
seus respetivos Users. Para simplificar as operações iremos trabalhar sempre com
valores inteiros em cêntimos.

A classe ExpensesGroup implementa o grupo de Despesas efetuadas por um grupo
de utilizadores. Deve permitir adicionar um User ao grupo, adicionar uma despesa
por um User pertencente ao grupo, saber quanto deve/tem a receber um determinado
User aos/dos restantes e permitir registar que um User saldou a sua divida a outro
User ao qual devia. Esta classe deve guardar a informação de quanto cada User
deve a outro User num Map<String,Map<String, Integer>> que tem como
chave o nome do utilizador do User recetor (a quem é devido) e como valor um outro
Map que tem como chave o nome do utilizador do User devedor e valor um Integer
com o valor devido.
