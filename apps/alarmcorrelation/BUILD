COMPILE_DEPS = CORE_DEPS + [
    "//drivers/utilities:onos-drivers-utilities",  # Enabled XMLParsing features
    "//core/common:onos-core-common",
    "//core/net:onos-core-net",  # enable ONOS core net packages for managing links...
    "//utils/rest:onlab-rest",
    "//core/store/serializers:onos-core-serializers",
    "//core/store/primitives:onos-core-primitives",
    "//protocols/netconf/api:onos-protocols-netconf-api",
    "//cli:onos-cli-native",  # Adding here to enable the data storage service be read from the program
    "//apps/faultmanagement/fmcli:onos-apps-faultmanagement-fmcli",  # Enabling AlarmManager stuff
    "//apps/faultmanagement/fmmgr:onos-apps-faultmanagement-fmmgr-native",
    "//apps/roadm/app:onos-apps-roadm-app",  # Enables RoadmService instance
    "//apps/optical-model:onos-apps-optical-model-native",  # Enables use of OpticalChannelUtility
]

osgi_jar_with_tests(
    deps = COMPILE_DEPS,
)

onos_app(
    app_name = "org.onosproject.alarmcorrelation",
    category = "Traffic Engineering",
    description = "This is proposition of application, which is capable of " +
                  "detecting failure states in the optical network. " +
                  "By now, only fiber cut scenario is implemented.",
    title = "Alarm Correlation",
    url = "http://onosproject.org",
)

