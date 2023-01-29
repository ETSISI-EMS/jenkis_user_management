import argparse
import json
import pandas as pd


def main():
    parser = argparse.ArgumentParser(description=__doc__)

    # add an argument to specify the input file that must be a csv file
    parser.add_argument("input_file", help="Input file in csv format")
    args = parser.parse_args()

    # read the input file in latin1 encoding
    df = pd.read_csv(args.input_file, encoding="utf-8-sig")

    data = {'users': df.to_dict(orient='records')}    
    # convert data to a json string in latin1 encoding
    json_string = json.dumps(data, ensure_ascii=False)

    # print the json string
    print(json_string)

if __name__ == "__main__":
    main()