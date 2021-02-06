package net.ashald.envfile.products.idea;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configuration.EnvironmentVariablesData;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterChangeListener;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.util.Consumer;
import com.jetbrains.nodejs.run.NodeJSRunConfigurationExtension;
import com.jetbrains.nodejs.run.NodeJSRuntimeSession;
import com.jetbrains.nodejs.run.NodeJsRunConfiguration;
import net.ashald.envfile.platform.ui.EnvFileConfigurationEditor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NodeJsEnvFileRunConfigurationExtension extends NodeJSRunConfigurationExtension {

    @Nullable
    @Override
    protected NodeJSRuntimeSession createLocalRuntimeSession(@NotNull final NodeJsRunConfiguration nodeJsRunConfiguration, @NotNull final ExecutionEnvironment executionEnvironment) throws ExecutionException {
        updateRunConfiguration(nodeJsRunConfiguration);
        return null;
        //return new MyNodeJSRuntimeSession(nodeJsRunConfiguration);
    }



    @Nullable
    @Override
    protected NodeJSRuntimeSession createRemoteRuntimeSession(@NotNull final NodeJsRunConfiguration nodeJsRunConfiguration,
                                                              @NotNull final ExecutionEnvironment executionEnvironment,
                                                              @NotNull final com.intellij.javascript.nodejs.NodeFileTransfer nodeFileTransfer) throws ExecutionException {
        updateRunConfiguration(nodeJsRunConfiguration);

        return null;
        //return new MyNodeJSRuntimeSession(nodeJsRunConfiguration);
    }

    private static void updateRunConfiguration(NodeJsRunConfiguration nodeJsRunConfiguration) throws ExecutionException {
        EnvironmentVariablesData evd = nodeJsRunConfiguration.getEnvData();
        Map<String, String> newEnv = EnvFileConfigurationEditor.collectEnv(nodeJsRunConfiguration,  new LinkedHashMap<>(evd.getEnvs()));

        nodeJsRunConfiguration.setEnvData(EnvironmentVariablesData.create(newEnv, evd.isPassParentEnvs()));
    }

    @Override
    public boolean isApplicableFor(@NotNull final NodeJsRunConfiguration configuration) {
        return true;
    }

    @Override
    public boolean isEnabledFor(@NotNull final NodeJsRunConfiguration applicableConfiguration, @Nullable final RunnerSettings runnerSettings) {
        return true;
    }

    @Nullable
    public String getEditorTitle() {
        return EnvFileConfigurationEditor.getEditorTitle();
    }


    @Nullable
    protected <P extends NodeJsRunConfiguration> SettingsEditor<P> createEditor(@NotNull final P configuration, final Consumer<NodeJsInterpreterChangeListener> listenerRegistrar) {
        return new EnvFileConfigurationEditor<>(configuration);
    }

    private static class MyNodeJSRuntimeSession implements NodeJSRuntimeSession {

        private final NodeJsRunConfiguration configuration;

        public MyNodeJSRuntimeSession(NodeJsRunConfiguration configuration) {
            this.configuration = configuration;
        }
        @Override
        public ProcessHandler createProcessHandler(final GeneralCommandLine commandLine, final int debugPort, final List<Integer> openPorts) throws ExecutionException {
            Map<String, String> newEnv = EnvFileConfigurationEditor.collectEnv(configuration,  new LinkedHashMap<>(commandLine.getEnvironment()));

            commandLine.getEnvironment().putAll(newEnv);
            return null;
        }
    }

}
