
BUILD_DIR=$(PWD)/build
INCLUDE_DIR=$(PWD)/include
LIB_ARM_DIR=$(PWD)/lib/arm
LIB_X86_DIR=$(PWD)/lib/x86

KT=$(PWD)/../translator/build/libs/translator-1.0.jar
LLINK=llvm-link-3.6

LLINK_FLAGS=-S

KT_ALL_DEPS=java -jar $(KT)
LLINK_ALL_DEPS=$(LLINK) $(LLINK_FLAGS) $(filter %.ll,$^) > $@

all: $(BUILD_DIR)/stdlib_arm.ll $(BUILD_DIR)/stdlib_x86.ll

$(BUILD_DIR)/stdlib_x86.ll: $(BUILD_DIR)/classes_x86.ll $(LIB_X86_DIR)/*.ll
	$(LLINK_ALL_DEPS)

$(BUILD_DIR)/stdlib_arm.ll: $(BUILD_DIR)/classes_arm.ll $(LIB_ARM_DIR)/*.ll
	$(LLINK_ALL_DEPS)

$(BUILD_DIR)/classes_x86.ll: $(INCLUDE_DIR)/*.kt
	$(KT_ALL_DEPS) -o $@ $(filter %.kt,$^)

$(BUILD_DIR)/classes_arm.ll: $(INCLUDE_DIR)/*.kt
	$(KT_ALL_DEPS) --arm -o $@ $(filter %.kt,$^)

clean:
	rm $(BUILD_DIR)/*

.PHONY: all clean
