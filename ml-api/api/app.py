from flask import Flask  , request , jsonify
import pickle
import numpy as np
import sklearn

app = Flask(__name__)
@app.route("/predict", methods=['POST'])
def predict():
    data = request.get_json()

    modele = data['nom_de_modele']

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
    donner_du_modele=np.array([liste_des_patients])
    le_chemin_de_fichier= f"./models/{modele}"

    with open(le_chemin_de_fichier,"rb") as fichier :
        notre_modele = pickle.load(fichier)
    
    prediction = notre_modele.predict(donner_du_modele)[0]

    if prediction == 1 :
        conclusion = "ckd"
    else :
        conclusion = "notckd"

    return jsonify({"prediction" :conclusion})

if __name__ == "__main__":
    app.run(debug=True,port=5000)