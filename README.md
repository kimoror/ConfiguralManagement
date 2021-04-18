Search dependence of python libraries
Result: text of library dependences in graphiz format

download(String urlStr, String file): It accepts a link to the site (https://pypi.org/simple/ + package name) and the path to the folder where to upload the package. The function loads the file to the specified path.

getWhl(String pathF, String urlF, String pkgNameF, SAXParser parserF): It takes the path to the directory, a link to the file (to pass it to the download function, the name of the package (which you need to download the whl archive and the parser). It creates a handler object (the functionality of which is described in the XMLHandler class), parses the html document (it contains links to the archive) that we uploaded, and loads the whl archive with dependencies.

getGraph(String pkgNameF, String strF): It is fed the name of the parent package and the name of the dependency.

Main function: We are creating a parser. Next, we ask you to enter the name of the package from the keyboard, set the path to the site (https://pypi.org/simple/) and the path to the folder where we will store the dependencies and create a folder where we will store the dependencies. Call the getWhl function, unpack the resulting archive, search for the METADATA file and write the path to it in the metaPath variable. We go through METADATA and look for strings that contain "Requires-Dist" and do not contain "extra". If we found it, then we remove all unnecessary items from the string and leave only the necessary name of the dependency. Call the getGraph(String pkgNameF, String strF) and add a dependency to the ArrayList (in depend we store the dependencies of the root package). Next, we open the graph, call the addittionalDepends(String pkgNameF, ArrayList<String> dependF, String pathF, String urlF, SAXParser parserF), which is recursive and goes through all the elements passed by the ArrayList depends, its functionality repeats main, only when we find a new dependency, we enter the recursion. After exiting additionalDepends, we close the graph and delete the folder with dependencies
