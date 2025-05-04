from flask import Flask, request, jsonify
from PIL import Image
import pytesseract
import os
import re

app = Flask(__name__)

pytesseract.pytesseract.tesseract_cmd = r"C:\Program Files\Tesseract-OCR\tesseract.exe"

#THESE ARE SPECIFIC TO DUNKIN DONUTS
# format: 'digits item_name digit.2digits'
item_pattern = re.compile(r'^(\d+)\s+(.+?)\s+(\d+\.\d{2})$')
# format: 'Payment $digits.2digits'
subtotal_pattern = re.compile(r'\bPayment\s+\$(\d+\.\d{2})?', re.IGNORECASE)


@app.route('/ocr', methods=['POST'])
def ocr():
  if 'image' not in request.files:
    return jsonify({'error': 'No image file provided'}), 400

  image_file = request.files['image']
  if image_file.filename == '':
    return jsonify({'error': 'No selected file'}), 400

  try:
    image = Image.open(image_file)
    #perform OCR
    text = pytesseract.image_to_string(image)

    lines = text.split('\n')
    items = []
    subtotal = None

    for line in lines:
      line = line.strip()

      item_match = item_pattern.match(line)
      if item_match:
        qty = item_match.group(1) or "1"
        name = item_match.group(2)
        price = float(item_match.group(3))
        items.append({"name": name, "qty": qty, "price": price})
        continue

      # match subtotal line
      subtotal_match = subtotal_pattern.match(line)
      if subtotal_match:
        subtotal = float(subtotal_match.group(1))

    return jsonify({'items': items, 'subtotal': subtotal})
  except Exception as e:
    return jsonify({'error': str(e)}), 500

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5050)