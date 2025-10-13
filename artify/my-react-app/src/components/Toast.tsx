import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faXmark as faXMarkSolid } from "@fortawesome/free-solid-svg-icons";

import { useToast } from "../ToastContext";

const Toast: React.FC = () => {
  const { toasts, removeToast } = useToast();
  console.log("Rendering toasts:", toasts);

  return (
    <div
      style={{
        position: "fixed",
        top: "60px",
        right: 0,
        padding: "10px",
        zIndex: 9999,
      }}
    >
      {toasts.map((toast) => {
        console.log("Rendering toast:", toast);
        return (
          <div
            key={toast.id}
            className={`alert ${
              toast.type === "success" ? "alert-success" : "alert-danger"
            }`}
            style={{
              padding: "7px",
              marginBottom: "10px",
              borderRadius: "5px",
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
              width: "220px",
            }}
            role="alert"
            aria-live="assertive"
            aria-atomic="true"
          >
            <div>{toast.message}</div>
            <button
              onClick={() => removeToast(toast.id)}
              style={{
                background: "transparent",
                border: "none",
                cursor: "pointer",
                fontSize: "22px",
              }}
            >
              <FontAwesomeIcon icon={faXMarkSolid} />
            </button>
          </div>
        );
      })}
    </div>
  );
};

export default Toast;
