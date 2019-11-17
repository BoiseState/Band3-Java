package band;

import javax.swing.JButton;
import javax.swing.JLabel;
import org.jdesktop.beansbinding.AbstractBindingListener;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.Binding.SyncFailure;

public class LoggingBindingListener extends AbstractBindingListener {
    private JLabel outputLabel;
    private JButton okButton;

    LoggingBindingListener(JLabel outputLabel, JButton okButton) {
        if (outputLabel == null || okButton == null) throw new IllegalArgumentException();
        this.outputLabel = outputLabel;
        this.okButton = okButton;
    }

    @Override
    public void syncFailed(Binding binding, SyncFailure fail) {
        String description;
        if ((fail != null) && (fail.getType() == Binding.SyncFailureType.VALIDATION_FAILED)) {
            description = fail.getValidationResult().getDescription();
        } else {
            description = "Sync failed!";
        }
        outputLabel.setText(description);
        okButton.setEnabled(false);
    }

    @Override
    public void synced(Binding binding) {
        //String bindName = binding.getName();
        //String msg = "[" + bindName + "] Synced";
        outputLabel.setText("");
        okButton.setEnabled(true);
    }

}
