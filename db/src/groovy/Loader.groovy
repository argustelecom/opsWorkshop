import org.dbmaintain.config.DbMaintainConfigurationLoader

class Loader extends DbMaintainConfigurationLoader {
    @Override
    public Properties loadPropertiesFromFile(File propertiesFile) {
        return super.loadPropertiesFromFile(propertiesFile);
    }

    public void savePropertiesToFile(Properties props, File file) {
        file.createNewFile();

        def fos = new FileOutputStream(file);
        def osw = new OutputStreamWriter(fos, 'UTF-8');
        def bw = new BufferedWriter(osw);
        try {
            def keys = new ArrayList<>(props.keySet());
            Collections.sort(keys);

            store(props, keys, bw);
        } finally {
            bw?.close();
            osw?.close();
            fos?.close();
        }
    }

    private void store(Properties props, List<String> orderedKeys, BufferedWriter bw) {
        bw.write("#" + new Date().toString());
        bw.newLine();
        orderedKeys.each{key ->
            def escapedKey = saveConvert(key, true, true);
            def escapedVal = saveConvert((String)props.get(key), false, true);
            bw.write(escapedKey + "=" + escapedVal);
            bw.newLine();
        }
        bw.flush();
    }

    /* ************************************************************
     *               Украдено из класса Properties
     ************************************************************ */
    private String saveConvert(String theString,
                               boolean escapeSpace,
                               boolean escapeUnicode) {
        int len = theString.length();
        int bufLen = len * 2;
        if (bufLen < 0) {
            bufLen = Integer.MAX_VALUE;
        }
        StringBuffer outBuffer = new StringBuffer(bufLen);

        for(int x=0; x<len; x++) {
            char aChar = theString.charAt(x);

            if ((aChar > 61) && (aChar < 127)) {
                if (aChar == '\\') {
                    outBuffer.append('\\'); outBuffer.append('\\');
                    continue;
                }
                outBuffer.append(aChar);
                continue;
            }
            switch(aChar) {
                case ' ':
                    if (x == 0 || escapeSpace)
                        outBuffer.append('\\');
                    outBuffer.append(' ');
                    break;
                case '\t':outBuffer.append('\\'); outBuffer.append('t');
                    break;
                case '\n':outBuffer.append('\\'); outBuffer.append('n');
                    break;
                case '\r':outBuffer.append('\\'); outBuffer.append('r');
                    break;
                case '\f':outBuffer.append('\\'); outBuffer.append('f');
                    break;
                case '=':
                case ':':
                case '#':
                case '!':
                    outBuffer.append('\\'); outBuffer.append(aChar);
                    break;
                default:
                    if (((aChar < 0x0020) || (aChar > 0x007e)) & escapeUnicode ) {
                        outBuffer.append('\\');
                        outBuffer.append('u');
                        outBuffer.append(toHex((aChar >> 12) & 0xF));
                        outBuffer.append(toHex((aChar >>  8) & 0xF));
                        outBuffer.append(toHex((aChar >>  4) & 0xF));
                        outBuffer.append(toHex( aChar        & 0xF));
                    } else {
                        outBuffer.append(aChar);
                    }
            }
        }
        return outBuffer.toString();
    }

    private static char toHex(int nibble) {
        return hexDigit[(nibble & 0xF)];
    }

    private static final char[] hexDigit = ['0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'];
}
