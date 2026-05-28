FROM python:3.11-slim
WORKDIR /app
COPY api/requirements.txt .
COPY dataset/preprocessed_kidney_disease_data.csv .
RUN pip install --no-cache-dir -r requirements.txt
CMD ["python", "app.py"]
