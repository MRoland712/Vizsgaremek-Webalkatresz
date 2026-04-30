
from rembg import remove
from PIL import Image
import os
from pathlib import Path

# Mappa ahol a képek vannak
input_folder = "kepek"
output_folder = r"C:/carcompsImages/parts"

# Output mappa létrehozása
Path(output_folder).mkdir(exist_ok=True)

# Összes kép feldolgozása
for filename in os.listdir(input_folder):
    if filename.lower().endswith((".jpg", ".png", ".jpeg", ".bmp")):
        try:
            input_path = os.path.join(input_folder, filename)
            output_path = os.path.join(output_folder, f"{Path(filename).stem}.png")
            
            print(f"Feldolgozás: {filename}...")
            input_img = Image.open(input_path)
            output = remove(input_img)
            output.save(output_path)
            print(f"✓ Kész: {output_path}")
        except Exception as e:
            print(f"✗ Hiba: {filename} - {e}")

print("Kész!")