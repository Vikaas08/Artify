import { Form, Button } from "react-bootstrap";
import { useNavigate, Link } from "react-router-dom";
import { useEffect, useState } from "react";
import axios from "axios";
import Header from "../components/Header";
import Footer from "../components/Footer";
import { useAuth } from "../AuthContext";

const Login = () => {
  const [formData, setFormData] = useState({
    email: "",
    password: "",
  });
  const [validate, setValidate] = useState({
    email: "",
    password: "",
  });
  const [disable, setDisable] = useState(true);
  const [isChanged, setIsChanged] = useState(false);

  const { login } = useAuth();
  const navigate = useNavigate();

  const checkErrors = () => {
    let hasErr = false;
    const { email, password } = formData;
    const newErrors = { email: "", password: "" };
    const pattern = new RegExp("^[\\w.-]+@([\\w-]+\\.)+[\\w-]{2,}$");

    if (!email || email === "") {
      newErrors.email = "Email is required.";
      hasErr = true;
    } else if (!pattern.test(email)) {
      newErrors.email = "Email must be valid.";
      hasErr = true;
    }

    if (!password || password === "") {
      newErrors.password = "Password is required.";
      hasErr = true;
    }

    setValidate(newErrors);
    return hasErr;
  };

  useEffect(() => {
    if (isChanged) setDisable(checkErrors());
  }, [formData, isChanged]);

  const handleChange = (event: any) => {
    // const name = event.target.name;
    // if (name === "Email") {
    //   setEmail(event.target.value);
    // } else if (name === "Password") {
    //   setPassword(event.target.value);
    // }
    const { name, value } = event.target;
    setIsChanged(true);
    setFormData((prevValue) => {
      return {
        ...prevValue,
        [name]: value,
      };
    });
    setValidate((prevValue) => {
      return {
        ...prevValue,
        [name]: "",
      };
    });
  };

  const handleSubmit = (event: any) => {
    event.preventDefault();

    const formErrors = checkErrors();
    if (!formErrors) {
      axios
        .post(
          "https://hw3-cs571-spring-25.uw.r.appspot.com/api/login",
          {
            email: formData.email,
            password: formData.password,
          },
          { withCredentials: true }
        )
        .then((response) => {
          console.log("Login Form Submitted Successfully", response);

          if (Object.keys(response.data).length === 0) {
            setValidate((prevValue) => {
              return {
                ...prevValue,
                password: "Password or email is incorrect.",
              };
            });
          } else {
            console.log("Login successful", response.data);
            const { token, user } = response.data;
            login(user, token);

            navigate("/");
          }
        })
        .catch((err) => {
          console.log("Error while submitting the login form", err);
          if (err.response) {
            console.log("Response data:", err.response.data);
            console.log("Status code:", err.response.status);
            console.log("Headers:", err.response.headers);
          } else if (err.request) {
            console.log("No response received:", err.request);
          } else {
            console.log("Error message:", err.message);
          }
        });
    }
  };

  return (
    <div>
      <Header />
      <div className="container mt-5 mb-5">
        <div className="row justify-content-center">
          <div className="col-md-4">
            <div className="px-4 py-4 d-grid gap-2 border border-2 rounded">
              <Form noValidate onSubmit={handleSubmit}>
                <h1 className="pb-4">Login</h1>

                <Form.Group className="mb-2">
                  <Form.Label className="fw-semibold">Email address</Form.Label>
                  <Form.Control
                    type="email"
                    name="email"
                    placeholder="Enter email"
                    required={true}
                    onChange={handleChange}
                    value={formData.email}
                    isInvalid={!!validate.email}
                  />
                  <Form.Control.Feedback type="invalid">
                    {validate.email}
                  </Form.Control.Feedback>
                </Form.Group>
                <Form.Group className="mb-4">
                  <Form.Label className="fw-semibold">Password</Form.Label>
                  <Form.Control
                    type="password"
                    name="password"
                    placeholder="Password"
                    required={true}
                    onChange={handleChange}
                    value={formData.password}
                    isInvalid={!!validate.password}
                  />
                  <Form.Control.Feedback type="invalid">
                    {validate.password}
                  </Form.Control.Feedback>
                </Form.Group>
                <div className="d-grid gap-2">
                  <Button
                    className=""
                    variant="primary"
                    type="submit"
                    disabled={disable}
                  >
                    Log in
                  </Button>
                </div>
              </Form>
            </div>
            <p className="mt-2 text-center">
              Don't have an account yet? <Link to="/register">Register</Link>
            </p>
          </div>
        </div>
      </div>
      <Footer />
    </div>
  );
};

export default Login;
