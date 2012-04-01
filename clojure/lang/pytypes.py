"""pytypes.py

Saturday, March 24 2012
"""

import re                       # for compiled regex type
import sys                      # for file type
import new                      # for code
import types                    # for generators, etc.

pyObjectType = type(object())
pyRegexType = type(re.compile(""))
pyFuncType = type(lambda x : x)
pyListType = type([])
pySetType = type(set())
pyTupleType = type(())
pyDictType = type({})
pyStrType = type("")
pyUnicodeType = type(u"")
pyNoneType = type(None)
pyBoolType = type(True)
pyIntType = type(int())
pyLongType = type(long())
pyFloatType = type(float())
pyFileType = type(sys.stdin)
pyTypeType = type
pyTypeCode = new.code
pyTypeGenerator = types.GeneratorType

# add more if needed
