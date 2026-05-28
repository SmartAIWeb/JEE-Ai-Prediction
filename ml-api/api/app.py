from flask import Flask  , request , jsonify
import pickle
import numpy as np
import sklearn

app = Flask(__name__)

@app.route("/predict", methods=['POST'])
def predict():
    try :
        # Recuperation du JSON et du modele choisi
        data = request.get_json()
        #l'extraction  du nom du modele
        modele = data['nom_de_modele']


        #Regroupement de toutes les caractéristiques dans l'ordre attendu par le modele
        liste_des_patients= [
            # Variables numériques
            data['age'], 
            data['blood_pressure'],
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

            #Variables catégorielles éclatées
            data['red_blood_cells_urine_missing'],
            data['red_blood_cells_urine_normal'],
            data['red_blood_cells_urine_abnormal'],

            data['pus_cells_normal'],
            data['pus_cells_abnormal'],
            data['pus_cells_missing'],
            
            data['pus_cell_clumps_notpresent'],
            data['pus_cell_clumps_present'],
            data['pus_cell_clumps_missing'],
            
            data['bacteria_notpresent'],
            data['bacteria_present'],
            data['bacteria_missing'],
            
            data['hypertension_yes'],
            data['hypertension_no'],
            data['hypertension_missing'],
            
            data['diabetes_mellitus_yes'],
            data['diabetes_mellitus_no'],
            data['diabetes_mellitus_missing'],
            
            data['coronary_artery_disease_no'],
            data['coronary_artery_disease_yes'],
            data['coronary_artery_disease_missing'],
            
            data['appetite_good'],
            data['appetite_poor'],
            data['appetite_missing'],
            
            data['pedal_edema_no'],
            data['pedal_edema_yes'],
            data['pedal_edema_missing'],
            
            data['anemia_no'],
            data['anemia_yes'],
            data['anemia_missing']
        ]

        # Formatage pour Scikit-Learn
        donner_du_modele=np.array([liste_des_patients])
        
        # Chargement du modele pickle selectionne
        le_chemin_de_fichier= f"./models/{modele}"

        
        with open(le_chemin_de_fichier,"rb") as fichier :
            notre_modele = pickle.load(fichier)

        # L'execution de la prediction (la recuperation du premier element du tableau d resultat)
        prediction = notre_modele.predict(donner_du_modele)[0]

        # La conversion du type NumPy vers Python pour le Json
        if hasattr(prediction, "item"):
            prediction = prediction.item()


        # Retour succes avec le status 200
        return jsonify({
            "status" : 200,
            "prediction" :prediction
            })
    
    except Exception as e:
        #Retour erreur avec le status 400
        return jsonify({
            "status" : 400,
            "error" :str(e)
            })


if __name__ == "__main__":
    app.run(debug=True,port=5000)