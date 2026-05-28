from flask import Flask, request, jsonify
import pickle
import numpy as np
import pandas as pd

app = Flask(__name__)

df = pd.read_csv('preprocessed_kidney_disease_data.csv')
FEATURE_ORDER = [col for col in df.columns if col not in ['patient_id', 'ckd_diagnosis']]

def reordering_features(user_info):
    # Reordering the features to match the training order
    return [user_info[col] for col in FEATURE_ORDER]

@app.route("/predict", methods=['POST'])
def predict():
    try:
        data = request.get_json()

        if (data["request_type"] != "prediction" or not data['nom_de_modele'] or not data["user_info"]):
            return jsonify({"error": "invalid request_type"}), 400

        modele_name = data['nom_de_modele']
        features = reordering_features(data['user_info'])
        donner_du_modele = np.array([features])

        # Chargement du modele pickle selectionne
        chemin_de_fichier = f"./models/{modele_name}"
        with open(chemin_de_fichier, "rb") as fichier:
            notre_modele = pickle.load(fichier)

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
