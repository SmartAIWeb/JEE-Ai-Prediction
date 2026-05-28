from flask import Flask  , request , jsonify
import pickle
import numpy as np
import sklearn
# L'initialisation de l'application Flask  
app = Flask(__name__)
# Definition de la route post pour recevoir le requetes de prediction  
@app.route("/predict", methods=['POST'])
def predict():
    #recuperation des donnees JSON envoyees par l'application  java jee 
    data = request.get_json()
    #l'extraction  du nom du modele
    modele = data['nom_de_modele']
    #l'extraction  des variables numerique
    age = data['age']
    urine_specific_gravity = data['urine_specific_gravity']
    albumin = data['albumin']
    sugar = data['sugar']
    blood_glucose_random = data['blood_glucose_random']
    blood_urea = data['blood_urea']
    serum_creatinine = data['serum_creatinine']
    sodium = data['sodium']
    potassium = data['potassium']
    hemoglobin = data['hemoglobin']
    packed_cell_volume = data['packed_cell_volume']
    white_blood_cell_count = data['white_blood_cell_count']
    red_blood_cell_count = data['red_blood_cell_count']

    #l'extraction  des variables categorielles : convertion de 'yes/no en binaire 1/0'
    red_blood_cells_urine = 1 if data['red_blood_cells_urine'] == 'yes' else 0
    pus_cells = 1 if  data['pus_cells']  == 'yes' else 0
    pus_cell_clumps =1 if  data['pus_cell_clumps'] == 'yes' else 0
    bacteria = 1 if data['bacteria'] == 'yes' else 0
    hypertension = 1 if data['hypertension'] == 'yes' else 0
    diabetes_mellitus = 1 if data['diabetes_mellitus'] == 'yes' else 0
    coronary_artery_disease =1 if  data['coronary_artery_disease'] == 'yes' else 0
    appetite =1 if  data['appetite'] == 'yes' else 0
    pedal_edema =1 if  data['pedal_edema'] == 'yes' else 0
    anemia = 1 if data['anemia'] == 'yes' else 0

    #Regroupement de toutes les caractéristiques dans l'ordre attendu par le modele
    liste_des_patients= [
        age, 
        urine_specific_gravity, 
        albumin, 
        sugar, 
        blood_glucose_random, 
        blood_urea, 
        serum_creatinine, 
        sodium, 
        potassium, 
        hemoglobin, 
        packed_cell_volume, 
        white_blood_cell_count, 
        red_blood_cell_count,
        red_blood_cells_urine, 
        pus_cells, 
        pus_cell_clumps, 
        bacteria, 
        hypertension, 
        diabetes_mellitus, 
        coronary_artery_disease, 
        appetite, 
        pedal_edema, 
        anemia
    ]

    # La conversion de la liste en tableau NumPy de dimension 2
    donner_du_modele=np.array([liste_des_patients])
    # La construction du chemin dynamique vers le fichier du modele selectionne
    le_chemin_de_fichier= f"./models/{modele}"

    # L'ouverture et chargement du modele entraine avec Pickle
    with open(le_chemin_de_fichier,"rb") as fichier :
        notre_modele = pickle.load(fichier)
    # L'execution de la prediction (la recuperation du premier element du tableau d resultat)
    prediction = notre_modele.predict(donner_du_modele)[0]

    # L'interpretation du resultat binaire du modele
    if prediction == 1 :
        conclusion = "ckd"
    else :
        conclusion = "notckd"

    # Renvoi du résultat final sous format JSON
    return jsonify({"prediction" :conclusion})

# Lancement du serveur Flask sur le port 5000 avec le mode debug active
if __name__ == "__main__":
    app.run(debug=True,port=5000)