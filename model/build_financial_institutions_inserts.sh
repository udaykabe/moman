cat financial_institutions_fragments.txt | awk -F'>' -f parse_financial_institutions_from_trimmed_xml.awk
