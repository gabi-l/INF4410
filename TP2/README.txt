Fichier de configuration:

Le syst�me peut �tre configur� grace aux fichiers de configurations dans le r�pertoire ./config_dir/config.

- Serveur -
Pour les fichiers de configurations des serveurs, la premi�re ligne indique le port d'�coute (de 5000 � 5050 sur le r�seau de la Polytechnique), la deuxi�me ligne indique le niveau de surcharge du serveur et la troisi�me ligne indique le pourcentage de r�ponses malicieuses retourn�es par le serveur (de 0 � 100).

- Client -
Pour les fichiers de configurations des client, la premi�re ligne indique si le syst�me est en mode s�curis� ou non (1 pour le mode s�cuiris� et 0 pour le mode non s�curis�). Les lignes subs�quentes indique pour le premier champs l'adresse des serveurs de calcul et leur port d'�coute pour le deuxi�me champs (les champs sont s�parr�s par un caract�re d'espacement).

Exemples:
- server3.config: serveur �coutant sur le port 5011, ayant un niveau de surcharge commen�ant � 5 op�rations et �tant malicieux 50% temps.

5011
5
50

- client1.config: client int�ragissant dans un syst�me non s�curis� et ayant comme serveur de calcul les h�tes des adresses 132.207.12.43, 132.207.12.44 et 132.207.12.45, tous �coutant sur le port 5011.

0
132.207.12.43 5011
132.207.12.44 5011
132.207.12.45 5011



Lancement de l'application:

- Serveur -
Lors du lancement de l'application serveur, un seul param�tre est n�cessaire: le nom du fichier de configuration (seulement son nom, son chemin d'acc�s [path] �tant d�j� cod� dans l'application [./config_dir/config]).

- Client -
Lors du lancement de l'application client, deux param�tres sont n�cessaire: le nom du fichier de configuration et le nom du fichier contenant les op�rations � effectuer. Dans les deux cas, seulement le nom du fichier est n�cessaire (le chemin d'acc�s [path] �tant d�j� cod� dans l'application [./config_dir/config]). pour le fichier de configurations et [./config_dir/donnees] pour les op�rations).

Exemples:
- Serveur utilisant les configurations du fichier server3.config.
"./server server3.config"

- Client utilisant les configurations du fichier client1.config et effectuant les op�rations du fichier donnees-2317.txt
"./client client1.config donnees-2317.txt"