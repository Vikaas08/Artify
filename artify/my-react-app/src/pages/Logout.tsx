import axios from "axios";
// import { useToast } from "../ToastContext";

function Logout() {
  console.log("logout component called");
  axios
    .post(
      "https://hw3-cs571-spring-25.uw.r.appspot.com/api/logout",
      {},
      { withCredentials: true }
    )
    .then(() => {})
    .catch((err) => {
      console.log("error while logging out!", err);
    });

  return <div></div>;
}

export default Logout;
