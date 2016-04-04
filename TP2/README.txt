Fichier de configuration:

Le système peut être configuré grace aux fichiers de configurations dans le répertoire ./config_dir/config.

- Serveur -
Pour les fichiers de configurations des serveurs, la première ligne indique le port d'écoute (de 5000 à 5050 sur le réseau de la Polytechnique), la deuxième ligne indique le niveau de surcharge du serveur et la troisième ligne indique le pourcentage de réponses malicieuses retournées par le serveur (de 0 à 100).

- Client -
Pour les fichiers de configurations des client, la première ligne indique si le système est en mode sécurisé ou non (1 pour le mode sécuirisé et 0 pour le mode non sécurisé). Les lignes subséquentes indique pour le premier champs l'adresse des serveurs de calcul et leur port d'écoute pour le deuxième champs (les champs sont séparrés par un caractère d'espacement).

Exemples:
- server3.config: serveur écoutant sur le port 5011, ayant un niveau de surcharge commençant à 5 opérations et étant malicieux 50% temps.

5011
5
50

- client1.config: client intéragissant dans un système non sécurisé et ayant comme serveur de calcul les hôtes des adresses 132.207.12.43, 132.207.12.44 et 132.207.12.45, tous écoutant sur le port 5011.

0
132.207.12.43 5011
132.207.12.44 5011
132.207.12.45 5011



Lancement de l'application:

- Serveur -
Lors du lancement de l'application serveur, un seul paramètre est nécessaire: le nom du fichier de configuration (seulement son nom, son chemin d'accès [path] étant déjà codé dans l'application [./config_dir/config]).

- Client -
Lors du lancement de l'application client, deux paramètres sont nécessaire: le nom du fichier de configuration et le nom du fichier contenant les opérations à effectuer. Dans les deux cas, seulement le nom du fichier est nécessaire (le chemin d'accès [path] étant déjà codé dans l'application [./config_dir/config]). pour le fichier de configurations et [./config_dir/donnees] pour les opérations).

Exemples:
- Serveur utilisant les configurations du fichier server3.config.
"./server server3.config"

- Client utilisant les configurations du fichier client1.config et effectuant les opérations du fichier donnees-2317.txt
"./client client1.config donnees-2317.txt"