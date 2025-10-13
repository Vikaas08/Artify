import { Image } from "react-bootstrap";

function Footer() {
  return (
    <div className="footer">
      <a href="https://www.artsy.net/">
        Powered by{" "}
        <Image src="/artsy_logo.svg" alt="artsy logo" width={18} height={18} />{" "}
        Artsy.
      </a>
    </div>
  );
}

export default Footer;
