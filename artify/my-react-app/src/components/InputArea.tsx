import { useState } from "react";
import axios from "axios";
import { InputGroup, Form, Button, Spinner } from "react-bootstrap";

function InputArea(props: any) {
  const [disable, setDisable] = useState(true);
  const [spinner, setSpinner] = useState(false);

  const handleChange = (event: any) => {
    {
      const inputValue = event.target.value;
      props.handleChange(inputValue);
      setDisable(inputValue.trim() === "");
    }
  };

  const handleSubmit = (event: any) => {
    setSpinner(true);
    event.preventDefault();
    axios
      .get("https://hw3-cs571-spring-25.uw.r.appspot.com/api/search", {
        params: {
          userInput: props.userInput,
        },
      })
      .then((res) => {
        props.setSearchResult(res.data);
      })
      .catch((err) => {
        console.log("Error while sending search query", err);
      })
      .finally(() => {
        setSpinner(false);
      });

    props.setShowCaraousel(true);
  };

  const handleClear = () => {
    props.handleChange("");
    setDisable(true);
    props.setShowWiki(false);
    props.setShowCaraousel(false);
  };

  return (
    <div>
      <Form
        onSubmit={handleSubmit}
        className="d-flex justify-content-center mt-3 mb-4"
      >
        <InputGroup>
          <Form.Control
            type="text"
            name="userInput"
            placeholder="Please enter an artist name."
            required
            spellCheck="false"
            value={props.userInput}
            onChange={handleChange}
          />
          <Button
            className="searchButton d-flex align-items-center"
            // variant="primary"
            type="submit"
            disabled={disable}
          >
            {spinner && (
              <>
                Search
                <Spinner
                  as="span"
                  animation="border"
                  size="sm"
                  role="status"
                  aria-hidden="true"
                  className="mx-1"
                />
              </>
            )}
            {!spinner && "Search"}
          </Button>
          <Button variant="secondary" type="reset" onClick={handleClear}>
            Clear
          </Button>
        </InputGroup>
      </Form>
    </div>
  );
}

export default InputArea;
