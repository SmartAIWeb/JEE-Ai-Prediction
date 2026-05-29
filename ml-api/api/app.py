from flask import Flask, request, jsonify
import pickle
import numpy as np
import pandas as pd
import os 

app = Flask(__name__)

df = pd.read_csv('preprocessed_kidney_disease_data.csv')
FEATURE_ORDER = [col for col in df.columns if col not in ['patient_id', 'ckd_diagnosis']]

modele_cache = {}

def reordering_features(user_info):
    # Reordering the features to match the training order
    return [user_info[col] for col in FEATURE_ORDER]

@app.route("/predict", methods=['POST'])
def predict():
    try:
        data = request.get_json()

        # eviter le crash si une cle manque dans le JSON
        if not data or "request_type" not in data or "nom_de_modele" not in data or "user_info" not in data:
            return jsonify({"error": "invalid request format(missing keys)"}), 400

        if data["request_type"] != "prediction":
            return jsonify({"error": "invalid request_type value"}), 400

        modele_name = data['nom_de_modele']
        features = reordering_features(data['user_info'])
        donner_du_modele = np.array([features])

        # optimisation de performance 
        if modele_name in modele_cache:
            notre_modele = modele_cache[modele_name]
        else :
                
            # Chargement du modele pickle selectionne
            chemin_de_fichier = f"./models/{modele_name}"
            #verifier si le fichier exist physiquement 
            if not os.path.exists(chemin_de_fichier):
                return jsonify({"error": f"Model {modele_name} not found"}), 404
            with open(chemin_de_fichier, "rb") as fichier:
                notre_modele = pickle.load(fichier)

                # sauvegarde dans le cache pour la prochaine fois
                modele_cache[modele_name] = notre_modele

        # L'execution de la prediction
        prediction = notre_modele.predict(donner_du_modele)[0]

        # La conversion du type NumPy vers Python pour le Json
        if hasattr(prediction, "item"):
            prediction = prediction.item()

        # Retour succes avec le status 200
        return jsonify({"prediction": prediction}), 200

    except Exception as e:
        # Retour erreur avec le status 400
        return jsonify({"error": str(e)}), 400

if __name__ == "__main__":
    app.run(debug=True, port=5000)