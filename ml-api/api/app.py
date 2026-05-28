from flask import Flask  , request , jsonify
import pickle
import numpy as np
import sklearn

app = Flask(__name__)

@app.route("/predict", methods=['POST'])
def predict():
    # Recuperation du JSON et du modele choisi
    data = request.get_json()
    #l'extraction  du nom du modele
    modele = data['nom_de_modele']


    #Regroupement de toutes les caractéristiques dans l'ordre attendu par le modele
    liste_des_patients= [
        data['age'], 
        data['urine_specific_gravity'], 
        data['albumin'],
        data['sugar'],
        data['blood_glucose_random'],
        data['blood_urea'],
        data['serum_creatinine'],
        data['sodium'],
        data['potassium'],
        data['hemoglobin'],
        data['packed_cell_volume'],
        data['white_blood_cell_count'],
        data['red_blood_cell_count'],
        data['red_blood_cells_urine'],
        data['pus_cells'] ,
        data['pus_cell_clumps'],
        data['bacteria'],
        data['hypertension'],
        data['diabetes_mellitus'],
        data['coronary_artery_disease'],
        data['appetite'],
        data['pedal_edema'],
        data['anemia']
    ]

    # Formatage pour Scikit-Learn
    donner_du_modele=np.array([liste_des_patients])
    
    # Chargement du modele pickle selectionne
    le_chemin_de_fichier= f"./models/{modele}"

    
    with open(le_chemin_de_fichier,"rb") as fichier :
        notre_modele = pickle.load(fichier)

    # L'execution de la prediction (la recuperation du premier element du tableau d resultat)
    prediction = notre_modele.predict(donner_du_modele)[0]

    
    return jsonify({"prediction" :prediction})


if __name__ == "__main__":
    app.run(debug=True,port=5000)