import subprocess
import sys
import re
from datetime import datetime

# ─────────────────────────────────────────────
#  BEÁLLÍTÁS – ide írd a .bat fájl elérési útját
# ─────────────────────────────────────────────
BAT_FILE = r"C:\Users\ddori\Desktop\wildfly-preview-26.1.1.Final\bin\standalone.bat"
# ─────────────────────────────────────────────

# ANSI színkódok
RED   = "\033[91m"
RESET = "\033[0m"

# Minden sor formátuma: timestamp  LEVEL  [channel]  (task)  üzenet
LOG_PATTERN = re.compile(
    r'^(\d{2}:\d{2}:\d{2},\d{3})\s+(INFO|ERROR|WARN|DEBUG)\s+(\[\S+\])\s+(\(.*?\))\s+(.*)'
)

# Vizsgaremek-specifikus "at" sor az üzenetben
VIZSGAREMEK_AT = re.compile(r'^at deployment\.vizsgaremek-1\.0-SNAPSHOT\.war')

# Bármilyen "at ..." stack trace sor az üzenetben
ANY_AT = re.compile(r'^at [\w\$\.\[\]/]')


def parse_line(line):
    """Visszaadja a sor mezőit dict-ként, vagy None-t."""
    m = LOG_PATTERN.match(line.rstrip())
    if not m:
        return None
    return {
        "time":    m.group(1),
        "level":   m.group(2),
        "channel": m.group(3),
        "task":    m.group(4),
        "message": m.group(5).strip(),
    }


def print_info(entry):
    print(f"{entry['time']}  INFO  {entry['channel']}  {entry['task']}  {entry['message']}")


def print_error(entry):
    """ERROR sort piros színnel ír ki."""
    print(f"{RED}{entry['time']}  ERROR {entry['channel']}  {entry['task']}  {entry['message']}{RESET}")


def process_log_stream(stream):
    """
    Feldolgozási logika:

    Minden sornak van timestamp-je, ezért a szűrés az üzenet tartalma alapján történik:

    INFO  → mindig kiírja
    ERROR → az üzenet alapján dönt:
        - Nem "at ..." kezdetű  → ez a valódi exception fejléc → kiírja (piros)
        - "at deployment.vizsgaremek..." → releváns stack trace → kiírja (piros)
        - Bármely más "at ..." → java belső / jboss / undertow → KIHAGYJA
    """
    for raw in stream:
        entry = parse_line(raw)
        if not entry:
            continue  # üres sor vagy nem illeszkedő sor

        level = entry["level"]
        msg   = entry["message"]

        if level == "INFO":
            print_info(entry)

        elif level == "ERROR":
            if ANY_AT.match(msg):
                # Stack trace sor – csak vizsgaremek-eset nyomtatjuk
                if VIZSGAREMEK_AT.match(msg):
                    print_error(entry)
                # Minden más "at ..." sort csendben kihagyjuk
            else:
                # Exception fejléc (pl. java.lang.NumberFormatException: ...)
                print_error(entry)

        elif level in ("WARN", "DEBUG"):
            print_info(entry)


def run_bat_and_parse(bat_path):
    print("=" * 70)
    print(f"  LOG PARSER  –  {bat_path}")
    print(f"  Indítás: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    print("=" * 70)
    print()

    try:
        proc = subprocess.Popen(
            [bat_path],
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            text=True,
            encoding="utf-8",
            errors="replace",
            bufsize=1,
        )

        process_log_stream(proc.stdout)
        proc.wait()

        print()
        print("=" * 70)
        print(f"  Folyamat befejezve. Kilépési kód: {proc.returncode}")
        print("=" * 70)

    except FileNotFoundError:
        print(f"[HIBA] A .bat fájl nem található: {bat_path}")
        sys.exit(1)
    except PermissionError:
        print(f"[HIBA] Nincs jogosultság a fájl futtatásához: {bat_path}")
        sys.exit(1)


def parse_log_file(filepath):
    """Meglévő log fájl beolvasása és feldolgozása."""
    print("=" * 70)
    print(f"  LOG PARSER  –  fájl mód: {filepath}")
    print("=" * 70)
    print()
    try:
        with open(filepath, "r", encoding="utf-8", errors="replace") as f:
            process_log_stream(f)
    except FileNotFoundError:
        print(f"[HIBA] A log fájl nem található: {filepath}")
        sys.exit(1)


if __name__ == "__main__":
    # Használat:
    #   python log_parser.py              → BAT_FILE konstanst futtatja
    #   python log_parser.py script.bat   → megadott .bat fájlt futtat
    #   python log_parser.py server.log   → meglévő log fájlt dolgoz fel

    if len(sys.argv) > 1:
        target = sys.argv[1]
        if target.endswith(".bat"):
            run_bat_and_parse(target)
        else:
            parse_log_file(target)
    else:
        run_bat_and_parse(BAT_FILE)