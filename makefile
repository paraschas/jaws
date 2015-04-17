#
# Makefile created at Fri Apr 17 14:35:47 2015, by mmake
#

# Programs (with common options):
SHELL		= /bin/sh
CP		= cp
RM              = rm -f
MV              = mv -f
SED		= sed
ETAGS		= etags
XARGS		= xargs
CAT		= cat
FIND            = find
CPP		= cpp -C -P

INSTALL         = install
INSTALL_PROG    = $(INSTALL) -m $(MODE_PROGS)
INSTALL_FILE    = $(INSTALL) -m $(MODE_FILES)
INSTALL_DIR     = $(INSTALL) -m $(MODE_DIRS) -d

# Install modes 
MODE_PROGS      = 555
MODE_FILES      = 444
MODE_DIRS       = 2755

# Build programs
JAVAC           = javac
JAVADOC         = javadoc
JAR             = jar

# Build flags
JAVAC_FLAGS     = 
JAVADOC_FLAGS   = -version -author
JAR_FLAGS       = cvf0
JIKES_DEP_FLAG	= +M

# ------------------------------------------------------------------- #

# Prefix for every install directory
PREFIX		= 

# Where to start installing the class files. Set this to an empty value
#  if you dont want to install classes
CLASS_DIR	= $(PREFIX)classes

# The directory to install the jar file in. Set this to an empty value
#  if you dont want to install a jar file
JAR_DIR	        = $(PREFIX)lib

# The directory to install the app bundle in. Set this to an empty value
#  if you dont want to install an app bundle
BUNDLE_DIR      = $(PREFIX)lib


# The directory to install html files generated by javadoc
DOC_DIR         = $(PREFIX)docs

# The directory to install script files in
SCRIPT_DIR	= $(PREFIX)bin

# ------------------------------------------------------------------- #

# The name of the jar file to install
JAR_FILE        = 

# 
# The VERSION variable below should be set to a value 
# that will be tested in the .xjava code and Info.plist. 
# 
VERSION		= CHANGE_ME

# ------------------------------------------------------------------- #

# The name of the OS X Application Bundle to install
BUNDLE_FILE	= 

# Folder containing App Bundle resources (Info.plist, *.icns, etc.)
BUNDLE_RESOURCE_DIR = misc/macosx

# Items to copy to the Resources folder of the bundle
BUNDLE_RESOURCES = $(addsuffix .icns, $(basename $(APP_FILE)) Document)

# Location of JavaApplicatonStub
JAVA_STUB	= /System/Library/Frameworks/JavaVM.framework/Resources/MacOS/JavaApplicationStub

# ------------------------------------------------------------------- #

# Resource files:
#  Extend the list to install other files of your choice
RESOURCE_SRC	:= *.properties *.gif *.au

# Objects that should go into the jar file. (find syntax)
JAR_OBJS	:= \( -name '*.class' -o -name '*.gif' -o -name "*.au" \
		       -o -name '*.properties' \)

# Include the separate variables file if it exists
MAKEFILE_VARS	= makefile.vars
VARS	= $(wildcard $(MAKEFILE_VARS))
ifneq ($(VARS),)
	include $(MAKEFILE_VARS)
endif


# Packages we should compile
PACKAGES = \
	com.paraschas.ce325.web_server


# All packages that can be recursively compiled.
ALL_PACKAGES = \
	com.paraschas.ce325 \
	com.paraschas \
	com \
	$(PACKAGES)


# Packages to generate docs for.
JAVADOC_PACKAGES = $(PACKAGES)


# Resource packages
RESOURCES = \
	html.resources


# Directories with shell scripts
SCRIPTS = 

# ------------------------------------------------------------------- #

# A marker variable for the top level directory
TOPLEVEL	:= .

# Subdirectories with java files:
JAVA_DIRS	:= $(subst .,/,$(PACKAGES)) $(TOPLEVEL)

# Subdirectories with only resource files:
RESOURCE_DIRS	:= $(subst .,/,$(RESOURCES))

# All the .xjava source files:
XJAVA_SRC	:= $(foreach dir, $(JAVA_DIRS), $(wildcard $(dir)/*.xjava))

# All the xjava files to build
XJAVA_OBJS	:= $(XJAVA_SRC:.xjava=.java)

# Directory coresponding to a package
PACKAGE_DIR	= $(subst .,/,$(1))

# All the (x)java files in a package
PACKAGE_SRC	=  $(shell $(FIND) $(PACKAGE_DIR) \( -name '*.java' -or -name '*.xjava' \) )

# All the classes to build in a package
PACKAGE_OBJS	= $(patsubst %.java,%.class,$(PACKAGE_SRC: %.xjava=%.java))

# All the .java source files:
JAVA_SRC	:= $(foreach dir, $(JAVA_DIRS), $(wildcard $(dir)/*.java))
JAVA_SRC	:= $(XJAVA_OBJS) $(JAVA_SRC)

# Dependency files:
DEPEND_OBJS	:= $(JAVA_SRC:.java=.u)

# The intermediate java files and main classes we should build:
JAVA_OBJS	:= $(XJAVA_OBJS) $(JAVA_SRC:.java=.class)

#  Search for resource files in both JAVA_DIRS and RESOURCE_DIRS
RESOURCE_OBJS	:= $(foreach dir, $(JAVA_DIRS) $(RESOURCE_DIRS), \
		     $(wildcard $(foreach file, $(RESOURCE_SRC), \
		     $(dir)/$(file))))

# All the shell scripts source
SCRIPT_SRCS 	:= $(foreach dir, $(SCRIPTS), $(wildcard $(dir)/*.sh))
# All shell scripts we should install
SCRIPT_OBJS    	:= $(SCRIPT_SRCS:.sh=)

# All the files to install into CLASS_DIR
INSTALL_OBJS	:= $(foreach dir, $(JAVA_DIRS), $(wildcard $(dir)/*.class))
# Escape inner class delimiter $
INSTALL_OBJS	:= $(subst $$,\$$,$(INSTALL_OBJS))
# Add the resource files to be installed as well
INSTALL_OBJS	:= $(INSTALL_OBJS) $(RESOURCE_OBJS)


# ------------------------------------------------------------------- #


define check-exit
|| exit 1

endef


# -----------
# Build Rules
# -----------

%.java: %.xjava
	$(CPP) -D$(VERSION) $< $@

%.class: %.java
	$(JAVAC) $(JAVAC_FLAGS) $<

%.jar: $(JAVA_OBJS) $(RESOURCE_OBJS)
	$(FIND) $(TOPLEVEL) $(JAR_OBJS) -print | $(XARGS) \
	$(JAR) $(JAR_FLAGS) $(JAR_FILE) 

%.u: %.java
	$(JAVAC) $(JIKES_DEP_FLAG) $<


# -------
# Targets
# -------

.PHONY: all jar install uninstall doc clean depend tags bundle \
	help $(ALL_PACKAGES)

all::	$(JAVA_OBJS)

help:
	@echo "Usage: make {all|jar|srcjar|bundle|install|uninstall|doc|clean|depend|tags|PACKAGE_NAME}"
	@echo "	all: build all classes"
	@echo "	jar: build jar file"
	@echo "	srcjar: build source jar file"
	@echo "	bundle: build OS X app bundle"
	@echo "	install: install classes into $(CLASS_DIR)"
	@echo "		jar into $(JAR_DIR)"
	@echo "		bundle into $(BUNDLE_DIR)"
	@echo "		javadocs into $(DOC_DIR)"
	@echo "		scripts into $(SCRIPT_DIR)"
	@echo "	uninstall: remove installed files"
	@echo "	doc: generate api docs from javadoc comments"
	@echo "	clean: remove classes and temporary files"
	@echo "	depend: build class dependency info using jikes"
	@echo "	tags: build emacs tags file"
	@echo "	PACKAGE_NAME: builds all classes in this package and any subpackages."

# Jar target
ifneq ($(strip $(JAR_FILE)),)
jar:  $(JAR_FILE)
ifneq ($(strip $(JAR_DIR)),)
install:: $(JAR_FILE)
	@echo "===> [Installing jar file, $(JAR_FILE) in $(JAR_DIR)] "
	$(INSTALL_DIR) $(JAR_DIR) $(check-exit)
	$(INSTALL_FILE) $(JAR_FILE) $(JAR_DIR) $(check-exit)
uninstall::
	@echo "===> [Removing jar file, $(JAR_FILE) from $(JAR_DIR)] "
	$(RM) $(JAR_DIR)/$(JAR_FILE)  $(check-exit)
else
install::
	@echo "No jar install dir defined"
endif
clean::
	$(RM) $(JAR_FILE)
else
jar:
	@echo "No jar file defined"
endif

SRC_JAR_FILE := $(basename $(JAR_FILE))-src$(suffix $JAR_FILE)

# Source jar target
srcjar : $(SRC_JAR_FILE)
$(SRC_JAR_FILE): $(JAVA_SRC) $(RESOURCE_OBJS)
	$(FIND) $(TOPLEVEL) $(JAR_OBJS: .class=.java) -print | $(XARGS) \
	$(JAR) $(JAR_FLAGS) $@

# Bundle target
ifneq ($(strip $(BUNDLE_FILE)),)
bundle:  $(BUNDLE_FILE)
$(BUNDLE_FILE) : $(JAR_FILE)
	$(INSTALL_DIR) $(BUNDLE_FILE)/Contents/Resources/Java $(check-exit)
	$(INSTALL_DIR) $(BUNDLE_FILE)/Contents/MacOS $(check-exit)
	$(INSTALL_PROG) $(JAVA_STUB) $(BUNDLE_FILE)/Contents/MacOS/ \
		$(check-exit)
	( $(CAT) $(BUNDLE_RESOURCE_DIR)/Info.plist | $(SED) -e \
		s/VERSION/$(VERSION)/ >98762infoplist876 ) $(check-exit)
	$(INSTALL_FILE) 98762infoplist876 \
		$(BUNDLE_FILE)/Contents/Info.plist $(check-exit)
	$(RM) 98762infoplist876 $(check-exit)
	$(INSTALL_FILE) $(JAR_FILE) $(BUNDLE_FILE)/Contents/Resources/Java
	checkexit="";for f in $(BUNDLE_RESOURCES); do \
		$(INSTALL_FILE) $(BUNDLE_RESOURCE_DIR)$$f $(BUNDLE_FILE)/Contents/Resources/ \
		|| checkexit=$?; \
		done; test -z $$checkexit

ifneq ($(strip $(BUNDLE_DIR)),)
# This is probably bad, but I don't know how else to do it
install:: $(BUNDLE_FILE)
	@echo "===> [Installing app bundle, $(BUNDLE_FILE) in $(BUNDLE_DIR)] "
	$(INSTALL_DIR) $(BUNDLE_DIR) $(check-exit)
	$(CP) -R $(BUNDLE_FILE) $(BUNDLE_DIR) $(check-exit)
	$(INSTALL_FILE) $(BUNDLE_FILE) $(BUNDLE_DIR) $(check-exit)
uninstall::
	@echo "===> [Removing bundle file, $(BUNDLE_FILE) from $(BUNDLE_DIR)] "
	$(RM) -r $(BUNDLE_DIR)/$(BUNDLE_FILE)  $(check-exit)
else
install::
	@echo "No bundle install dir defined"
endif
clean::
	$(RM) -r $(BUNDLE_FILE)
else
bundle:
	@echo "No bundle file defined"
endif


# Install target for Classes and Resources 
ifneq ($(strip $(CLASS_DIR)),)
install:: $(JAVA_OBJS)
	@echo "===> [Installing classes in $(CLASS_DIR)] "
	$(INSTALL_DIR) $(CLASS_DIR) $(check-exit)
	$(foreach dir, $(JAVA_DIRS) $(RESOURCE_DIRS), \
		$(INSTALL_DIR) $(CLASS_DIR)/$(dir) $(check-exit))
	$(foreach file, $(INSTALL_OBJS), \
		$(INSTALL_FILE) $(file) $(CLASS_DIR)/$(file) \
	$(check-exit))
uninstall::
	@echo "===> [Removing class-files from $(CLASS_DIR)] "
	$(foreach file, $(INSTALL_OBJS), \
		$(RM) $(CLASS_DIR)/$(file) \
	$(check-exit))
else
# Print a warning here if you like. (No class install dir defined)
endif



# Depend target
ifeq ($(findstring jikes,$(JAVAC)),jikes)
depend: $(XJAVA_OBJS) $(DEPEND_OBJS)
	( $(CAT) $(DEPEND_OBJS) |  $(SED) -e '/\.class$$/d' \
	  -e '/.*$$.*/d' > $(MAKEFILE_DEPEND); $(RM) $(DEPEND_OBJS); )
else
depend:
	@echo "mmake needs the jikes compiler to build class dependencies"
endif



# Doc target
ifneq ($(strip $(JAVADOC_PACKAGES)),)
doc:	$(JAVA_SRC)
	@echo "===> [Installing java documentation in $(DOC_DIR)] "
	$(INSTALL_DIR) $(DOC_DIR) $(check-exit)
	$(JAVADOC) $(JAVADOC_FLAGS) -d $(DOC_DIR) $(JAVADOC_PACKAGES)
else
doc:
	@echo "You must put your source files in a package to run make doc"
endif



# Script target
ifneq ($(strip  $(SCRIPT_OBJS)),)
all::	 $(SCRIPT_OBJS)
ifneq ($(strip $(SCRIPT_DIR)),)
install:: $(SCRIPT_OBJS)
	@echo "===> [Installing shell-scripts in $(SCRIPT_DIR)] "
	$(INSTALL_DIR) $(SCRIPT_DIR) $(check-exit)
	$(foreach file, $(SCRIPT_OBJS), \
		$(INSTALL_PROG) $(file) $(SCRIPT_DIR) $(check-exit))
uninstall:: 
	@echo "===> [Removing shell-scripts from $(SCRIPT_DIR)] "
	$(foreach file, $(SCRIPT_OBJS), \
		$(RM) $(SCRIPT_DIR)/$(file) $(check-exit))
else
# Print a warning here if you like. (No script install dir defined)
endif
clean::
	rm -f $(SCRIPT_OBJS)
endif



# Tag target
tags:	
	@echo "Tagging"
	$(ETAGS) $(filter-out $(XJAVA_OBJS), $(JAVA_SRC)) $(XJAVA_SRC)



# Various cleanup routines
clean::
	$(FIND) . \( -name '*~' -o -name '*.class' \) -print | \
	$(XARGS) $(RM) 
	$(FIND) . -name '*.u' -print | $(XARGS) $(RM)
	rm -rf $(DOC_DIR)

ifneq ($(strip $(XJAVA_SRC)),)
clean::
	$(RM) $(XJAVA_OBJS)
endif

# ----------------------------------------
# Include the dependency graph if it exist
# ----------------------------------------
MAKEFILE_DEPEND	= makefile.dep
DEPEND	= $(wildcard $(MAKEFILE_DEPEND))
ifneq ($(DEPEND),)
	include $(MAKEFILE_DEPEND)
endif

#package targets
com.paraschas.ce325 : $(call PACKAGE_OBJS,com.paraschas.ce325)
com.paraschas : $(call PACKAGE_OBJS,com.paraschas)
com : $(call PACKAGE_OBJS,com)
com.paraschas.ce325.web_server : $(call PACKAGE_OBJS,com.paraschas.ce325.web_server)


open:
	vim -p \
		Run.java \
		com/paraschas/ce325/web_server/ParseConfigurationFile.java \
		com/paraschas/ce325/web_server/Settings.java \
		config.xml \
		com/paraschas/ce325/web_server/Server.java \
		README.md \
		makefile


run: Run.java
	java Run
