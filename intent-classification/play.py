import re

text = "use ... to open ? i the geo-spatial the for the Graph me"

p = re.compile("[^a-zA-Z0-9_]")
stopword_filter = re.compile("[\\s+](a|an|the|for|to|and|of|you|me|in|i|please)(\\s+|$)")
space_trimmer = re.compile("\\s+")
textt = space_trimmer.sub(
    " ", stopword_filter.sub(
        " ", stopword_filter.sub(
            " ", space_trimmer.sub(
                " ", p.sub(
                    " ", text.lower())))))
# print(p.sub(" ", text.lower()))
# print(stopword_filter.sub(" ", p.sub(" ", text.lower())))

print(textt)
